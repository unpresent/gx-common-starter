package ru.gx.worker;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.gx.settings.SettingsController;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static lombok.AccessLevel.*;

/**
 * Класс исполнителя.<br/>
 * Запускает внутреннего Runner-а в отдельном потоке. Runner внутри себя бросает событие (spring-event),
 * обработчик которого должен содержать главную логику работы итераций. <br/>
 * Также запускает контроллера-демона, который следит за работой Runner-а, если второй зависает, то демон перезапускает Runner-а.
 *
 * @see AbstractOnIterationExecuteEvent
 * @see Worker
 */
@Slf4j
public abstract class AbstractWorker implements Worker {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    public final static String settingSuffixWaitOnStopMs = "wait_on_stop_ms";
    public final static String settingSuffixWaitOnRestartMs = "wait_on_restart_ms";
    public final static String settingSuffixMinTimePerIterationMs = "min_time_per_iteration_ms";
    public final static String settingSuffixTimoutRunnerLifeMs = "timeout_runner_life_ms";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Settings">
    /**
     * Название исполнителя. Используется в логировании.
     */
    @Getter
    @NotNull
    private final String serviceName;

    /**
     * ApplicationEventPublisher используется для бросания событий
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private ApplicationEventPublisher applicationEventPublisher;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SettingsController settingsController;

    /**
     * Имя настройки "Количество миллисекунд на ожидание останова"
     */
    @NotNull
    private final String settingNameWaitOnStopMs;

    /**
     * Имя настройки "Количество миллисекунд на ожидание перезапуска"
     */
    @NotNull
    private final String settingNameWaitOnRestartMs;

    /**
     * Имя настройки "Количество миллисекунд на таймаут, в течение которого Runner обязан отчитываться, что еще живой"
     */
    @NotNull
    private final String settingNameTimoutRunnerLifeMs;

    /**
     * Имя настройки "Минимальное количество миллисекунд на итерацию, если итерация завершается быстрее, то sleep"
     */
    @NotNull
    private final String settingNameMinTimePerIterationMs;

    /**
     * Признак того, что исполнителя требуется перезапускать автоматически, если он остановлен по каким-либо причинам
     */
    @Getter
    @Setter(PROTECTED)
    private volatile boolean autoRestart;

    /**
     * Внутренний исполнитель - работает в отдельном потоке. Содержит в себе цикл до сигнала выхода.
     */
    @Getter
    @Setter(PRIVATE)
    private volatile Runner runner;

    /**
     * Контроллер, который следит за тем, что исполнитель "живой" ("не завис")
     */
    @Getter(PROTECTED)
    private volatile RunnerTimerTaskController runnerTimerTaskController;

    /**
     * Таймер, который запускает периодически Контролера за Исполнителем
     */
    @Getter(PROTECTED)
    private volatile Timer timer;

    @Getter(PROTECTED)
    private volatile RestartingController restartingController = null;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути должен содержать логику итераций
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private AbstractOnIterationExecuteEvent iterationExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван перед запуском Исполнителя.
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private AbstractOnStartingExecuteEvent startingExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван после останова Исполнителя.
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private AbstractOnStoppingExecuteEvent stoppingExecuteEvent;

    /**
     * Признак того, что событие об основе Исполнителя уже вызывалось.
     * Требуется для разового вызова.
     * При останове исполнителя устанавливается в true.
     * При запуске Исполнителя сбрасывается в false.
     */
    @Getter(PROTECTED)
    private boolean stoppingExecuteEventCalled = false;

    /**
     * Минимальное время на итерацию. Если после выполнения итерации не требуется немедленно продолжить,
     * и время выполнения текущей итерации было менее указанного, то поток исполнителя засыпает.
     */
    protected int getMinTimePerIterationMs() {
        return this.settingsController.getIntegerSetting(this.settingNameMinTimePerIterationMs);
    }

    /**
     * Момент времени, когда Runner последний раз отчитывался, что работает.
     */
    @Getter
    private volatile long lastRunnerLifeCheckedMs = 0;

    /**
     * Метод, с помощью которого исполнитель отчитывается, что еще "жив".
     *
     * @see #lastRunnerLifeCheckedMs
     * @see #getTimoutRunnerLifeMs()
     */
    protected void runnerIsLifeSet() {
        this.lastRunnerLifeCheckedMs = System.currentTimeMillis();
    }

    /**
     * Определяющая настройка (в мс) период времени, в течение которого исполнитель обязан отчитаться о своей "жизни".
     * Если исполнитель не отчитается, то его требуется остановить (и при необходимости запустить снова).
     *
     * @see #runnerIsLifeSet()
     * @see #lastRunnerLifeCheckedMs
     * @see #autoRestart
     */
    protected int getTimoutRunnerLifeMs() {
        return this.settingsController.getIntegerSetting(this.settingNameTimoutRunnerLifeMs);
    }

    /**
     * Настройка (в мс), которая определяет сколько можно ждать штатного завершения исполнителя во время stop().
     *
     * @see #internalStop()
     * @see #internalWaitStop(int, boolean)
     */
    @Override
    public int getWaitOnStopMs() {
        return this.settingsController.getIntegerSetting(this.settingNameWaitOnStopMs);
    }

    /**
     * Настройка (в мс), которая определяет какую паузу надо выждать перед перезапуском после останова.
     *
     * @see RunnerTimerTaskController
     */
    @Override
    public int getWaitOnRestartMs() {
        return this.settingsController.getIntegerSetting(this.settingNameWaitOnRestartMs);
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Init">
    protected AbstractWorker(@NotNull final String serviceName) {
        this.serviceName = serviceName;
        this.settingNameWaitOnStopMs = getServiceName() + "." + settingSuffixWaitOnStopMs;
        this.settingNameWaitOnRestartMs = getServiceName() + "." + settingSuffixWaitOnRestartMs;
        this.settingNameMinTimePerIterationMs = getServiceName() + "." + settingSuffixMinTimePerIterationMs;
        this.settingNameTimoutRunnerLifeMs = getServiceName() + "." + settingSuffixTimoutRunnerLifeMs;
    }

    @PostConstruct
    public void init() {
        if (this.iterationExecuteEvent == null) {
            throw new NullPointerException("iterationExecuteEvent doesn't defined!");
        }
        if (this.startingExecuteEvent == null) {
            throw new NullPointerException("startingExecuteEvent doesn't defined!");
        }
        if (this.stoppingExecuteEvent == null) {
            throw new NullPointerException("stoppingExecuteEvent doesn't defined!");
        }
    }

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    @Override
    public abstract AbstractOnIterationExecuteEvent iterationExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при запуске Исполнителя.
     */
    @Override
    public abstract AbstractOnStartingExecuteEvent startingExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при останове Исполнителя.
     */
    @Override
    public abstract AbstractOnStoppingExecuteEvent stoppingExecuteEvent();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements Worker">

    @EventListener(AbstractDoStartWorkerEvent.class)
    public void onDoStartWorkerEvent(AbstractDoStartWorkerEvent __) {
        this.start();
    }

    @EventListener(AbstractDoStopWorkerEvent.class)
    public void onDoStopWorkerEvent(AbstractDoStopWorkerEvent __) {
        this.stop();
    }

    /**
     * Запуск исполнителя.
     */
    @Override
    public void start() {
        log.info("Starting start()");
        try {
            this.autoRestart = true;
            internalStart();
        } finally {
            log.info("Finished start()");
        }
    }

    /**
     * Принудительный останов после ожидания штатного завершения.
     */
    @Override
    public void stop() {
        log.info("Starting stop()");
        try {
            this.autoRestart = false;
            internalStop();
        } finally {
            log.info("Finished stop()");
        }
    }

    /**
     * Текущий статус исполнителя
     *
     * @return true = исполнитель запущен и работает
     */
    @Override
    public boolean isRunning() {
        final var runner = getRunner();
        return runner != null && runner.currentThread != null;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Internal methods for implements Worker">
    protected void internalStart() {
        if (isRunning()) {
            log.info("Runner " + getServiceName() + " already is running!");
            return;
        }

        synchronized (this) {
            log.info("starting Runner " + getServiceName());
            if (getRunner() == null) {
                if (getStartingExecuteEvent() != null) {
                    getApplicationEventPublisher().publishEvent(getStartingExecuteEvent());
                }
                createAndStartRunner();
                startRunnerTimerTaskController();
                log.info("Runner " + getServiceName() + " started");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                if (isRunning()) {
                    this.stoppingExecuteEventCalled = false;
                    log.info("Runner " + getServiceName() + " started success.");
                    return;
                }
            }
        }
        log.error("Runner " + getServiceName() + " not started!");
    }

    protected void internalStop() {
        log.info("Starting internalStop()");
        if (!isRunning()) {
            log.info("Runner " + getServiceName() + " already is stopped!");
            return;
        }

        synchronized (this) {
            internalWaitStop(getWaitOnStopMs(), true);
        }
        log.info("Finished internalStop()");
    }

    /**
     * Ожидание штатного завершения исполнителя в течение заданного времени.
     *
     * @param timeoutMs     время в течение которого надо подождать штатного завершения исполнителя.
     * @param withInterrupt требуется ли прервать выполнение исполнителя, если он не завершился за отведенное время.
     */
    private void internalWaitStop(int timeoutMs, boolean withInterrupt) {
        var runner = getRunner();
        if (runner == null) {
            return;
        }
        runner.isStopping.set(true);
        final var startWait = System.currentTimeMillis();
        try {
            while (getRunner() != null && System.currentTimeMillis() - startWait < timeoutMs) {
                // Ждем не более timeoutMs
                try {
                    Thread.sleep(timeoutMs / 10);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            if (withInterrupt && getRunner() != null) {
                // Прерываем
                runner = getRunner();
                final var thread = runner.currentThread;
                runner.currentThread = null;
                setRunner(null);
                if (thread != null) {
                    thread.interrupt();
                }
            }
        } finally {
            if (!isRunning()) {
                internalStopTimer();
                log.info("Runner " + getServiceName() + " is stopped success!");
            } else {
                log.info("Runner " + getServiceName() + " is not stopped!");
            }
            if (getStoppingExecuteEvent() != null) {
                this.stoppingExecuteEventCalled = true;
                getApplicationEventPublisher().publishEvent(getStoppingExecuteEvent());
            }
        }
    }

    /**
     * Останов timer-а
     */
    protected void internalStopTimer() {
        AbstractWorker.this.runnerTimerTaskController = null;
        if (AbstractWorker.this.timer != null) {
            synchronized (this) {
                log.info("Stopping timer...");
                final Timer timer;
                if ((timer = AbstractWorker.this.timer) != null) {
                    AbstractWorker.this.timer = null;
                    timer.cancel();
                }
                log.info("Timer stopped.");
                if (getStoppingExecuteEvent() != null) {
                    this.stoppingExecuteEventCalled = true;
                    getApplicationEventPublisher().publishEvent(getStoppingExecuteEvent());
                }
            }
        }
    }

    /**
     * Запуск исполнителя
     */
    protected void createAndStartRunner() {
        this.iterationExecuteEvent.setImmediateRunNextIteration(false);
        this.iterationExecuteEvent.setNeedRestart(false);
        this.iterationExecuteEvent.setStopExecution(false);
        runnerIsLifeSet();
        new Thread((this.runner = new Runner()), this.serviceName).start();
    }

    /**
     * Запуск потока перезапуска.
     */
    protected void startRestartingController() {
        if (getRestartingController() != null) {
            return;
        }
        synchronized (this) {
            if (getRestartingController() != null) {
                return;
            }
            new Thread((this.restartingController = new RestartingController()), this.serviceName + "-Restart").start();
        }
    }

    /**
     * Запуск контроллера за зависаниями исполнителя
     */
    protected void startRunnerTimerTaskController() {
        final var timeout = getTimoutRunnerLifeMs();
        this.runnerTimerTaskController = new RunnerTimerTaskController();
        this.timer = new Timer(getServiceName() + "-Timer", true);
        this.timer.scheduleAtFixedRate(this.runnerTimerTaskController, timeout, timeout / 10);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Исполнитель run() работает в своем потоке.
     */
    protected class Runner implements Runnable {
        private final AtomicBoolean isStopping = new AtomicBoolean(false);

        private volatile Thread currentThread;

        /**
         * Метод run с основным бесконечным циклом работы исполнителя
         */
        @SneakyThrows
        @Override
        public void run() {
            this.isStopping.set(false);
            this.currentThread = Thread.currentThread();
            log.info("Starting run()");
            try {
                if (getRestartingController() != null) {
                    log.info("Waiting release restartingController");
                    while (getRestartingController() != null) {
                        runnerIsLifeSet();
                        Thread.sleep(getMinTimePerIterationMs());
                    }
                    log.info("restartingController released!");
                }

                while (!this.isStopping.get()) {
                    iterationExecuteEvent.reset();
                    final var stepStarted = System.currentTimeMillis();

                    doIteration();
                    if (iterationExecuteEvent.isNeedRestart() || iterationExecuteEvent.isStopExecution()) {
                        log.info("break run(): iterationExecuteEvent.isNeedRestart() == " + iterationExecuteEvent.isNeedRestart() + "; iterationExecuteEvent.isStopExecution() == " + iterationExecuteEvent.isStopExecution());
                        break;
                    }

                    doIdleIfNeed(stepStarted);
                }
            } finally {
                this.currentThread = null;
                setRunner(null);

                if (iterationExecuteEvent.isStopExecution()) {
                    log.info("Finished run(): setAutoRestart(false)");
                    setAutoRestart(false);
                }
                if (iterationExecuteEvent.isNeedRestart()) {
                    log.info("After finished run(): startRestartingController()");
                    startRestartingController();
                }
            }
        }

        /**
         * Выполняет одну итерацию цикла обработки.
         */
        protected void doIteration() {
            log.debug("Starting doStep()");
            runnerIsLifeSet();
            getApplicationEventPublisher().publishEvent(iterationExecuteEvent);
            if (iterationExecuteEvent.isStopExecution()) {
                this.isStopping.set(true);
            }
            log.debug("Finished doStep(): iterationExecuteEvent.isStopExecution() == " + iterationExecuteEvent.isStopExecution()
                    + "; iterationExecuteEvent.isNeedRestart() == " + iterationExecuteEvent.isNeedRestart()
            );
        }

        /**
         * Выполняет необходимый простой, если время выполнения текущей итерации было меньше порогового.
         *
         * @param stepStarted - время начала выполнения текущей итерации
         * @see #getMinTimePerIterationMs()
         */
        protected void doIdleIfNeed(long stepStarted) {
            if (iterationExecuteEvent.isImmediateRunNextIteration()
                    || iterationExecuteEvent.isNeedRestart()
                    || iterationExecuteEvent.isStopExecution()
                    || this.isStopping.get()) {
                log.debug("doIdleIfNeed(): not sleep!");
                return;
            }

            long sleepTime;
            if ((sleepTime = getMinTimePerIterationMs() - (System.currentTimeMillis() - stepStarted)) > 0) {
                try {
                    runnerIsLifeSet();
                    log.debug("doIdleIfNeed(): sleep(" + sleepTime + ")!");
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Наблюдатель, который завершает (и при необходимости запускает) исполнителя, если тот "завис"
     *
     * @see #getTimoutRunnerLifeMs()
     * @see #lastRunnerLifeCheckedMs
     * @see #autoRestart
     */
    protected class RunnerTimerTaskController extends TimerTask {
        @SneakyThrows
        @Override
        public void run() {
            final var current = System.currentTimeMillis();
            log.debug("Starting TaskController.run():"
                    + " iterationExecuteEvent.isNeedRestart() == " + iterationExecuteEvent.isNeedRestart()
                    + "; iterationExecuteEvent.isStopExecution() == " + iterationExecuteEvent.isStopExecution());
            if (iterationExecuteEvent.isNeedRestart()
                    || iterationExecuteEvent.isStopExecution()
                    || current - getLastRunnerLifeCheckedMs() > getTimoutRunnerLifeMs()) {
                if (isRunning()) {
                    log.info("Before internalStop(); current == " + current
                            + "; getLastRunnerLifeCheckedMs() == " + getLastRunnerLifeCheckedMs()
                            + "; delta == " + (current - getLastRunnerLifeCheckedMs())
                            + "; iterationExecuteEvent.isNeedRestart() == " + iterationExecuteEvent.isNeedRestart()
                            + "; iterationExecuteEvent.isStopExecution() == " + iterationExecuteEvent.isStopExecution()
                    );
                    internalStop();
                }
                if (isAutoRestart() && !iterationExecuteEvent.isStopExecution()) {
                    startRestartingController();
                }
            }
        }
    }

    /**
     * Класс, который отвечает за процедуру перезапуска.
     */
    protected class RestartingController implements Runnable {
        @SneakyThrows
        @Override
        public void run() {
            final var wait = getWaitOnRestartMs();
            log.info("Restarting... Wait: {} ms", wait);
            AbstractWorker.this.iterationExecuteEvent.setImmediateRunNextIteration(false);
            AbstractWorker.this.iterationExecuteEvent.setNeedRestart(false);
            AbstractWorker.this.iterationExecuteEvent.setStopExecution(false);

            internalStopTimer();
            if (isRunning()) {
                internalStop();
            }
            final var waitTo = System.currentTimeMillis() + getWaitOnRestartMs();
            synchronized (AbstractWorker.this) {
                while (System.currentTimeMillis() < waitTo) {
                    log.debug("Restarting... waitLeft: {}", waitTo - System.currentTimeMillis());
                    runnerIsLifeSet();
                    Thread.sleep(getTimoutRunnerLifeMs() / 10);
                }
            }
            if (!isRunning()) {
                internalStart();
            }
            restartingController = null;
            log.info("Restarting finished!");
        }
    }
}

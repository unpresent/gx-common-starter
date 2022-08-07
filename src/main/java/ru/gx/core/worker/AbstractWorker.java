package ru.gx.core.worker;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.PostConstruct;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

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
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @NotNull
    private final MeterRegistry meterRegistry;

    /**
     * Название исполнителя. Используется в логировании.
     */
    @Getter
    @NotNull
    private final String workerName;

    @Getter
    @NotNull
    private final WorkerSettingsContainer settingsContainer;

    /**
     * ApplicationEventPublisher используется для бросания событий
     */
    @Getter(PROTECTED)
    @NotNull
    private final ApplicationEventPublisher applicationEventPublisher;

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
     * Признак того, что событие об основе Исполнителя уже вызывалось.
     * Требуется для разового вызова.
     * При останове исполнителя устанавливается в true.
     * При запуске Исполнителя сбрасывается в false.
     */
    @Getter(PROTECTED)
    private boolean stoppingExecuteEventCalled = false;

    /**
     * Момент времени, когда Runner последний раз отчитывался, что работает.
     */
    @Getter
    private volatile long lastRunnerLifeCheckedMs = 0;

    /**
     * Статистика исполнения.
     */
    @Getter
    private final AbstractWorkerStatisticsInfo statisticsInfo;

    /**
     * Метод, с помощью которого исполнитель отчитывается, что еще "жив".
     *
     * @see #lastRunnerLifeCheckedMs
     * @see WorkerSettingsContainer#getTimeoutRunnerLifeMs()
     */
    @Override
    public void runnerIsLifeSet() {
        this.lastRunnerLifeCheckedMs = System.currentTimeMillis();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractWorker(
            @NotNull final String workerName,
            @NotNull final WorkerSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final ApplicationEventPublisher applicationEventPublisher) {
        this.workerName = workerName;
        this.settingsContainer = settingsContainer;
        this.meterRegistry = meterRegistry;
        this.applicationEventPublisher = applicationEventPublisher;
        this.statisticsInfo = createStatisticsInfo();
    }

    @PostConstruct
    public void init() {
        if (this.getIterationExecuteEvent() == null) {
            throw new NullPointerException("iterationExecuteEvent doesn't defined!");
        }
        if (this.getStartingExecuteEvent() == null) {
            throw new NullPointerException("startingExecuteEvent doesn't defined!");
        }
        if (this.getStoppingExecuteEvent() == null) {
            throw new NullPointerException("stoppingExecuteEvent doesn't defined!");
        }
    }

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    @Override
    public abstract AbstractOnIterationExecuteEvent getIterationExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при запуске Исполнителя.
     */
    @Override
    public abstract AbstractOnStartingExecuteEvent getStartingExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при останове Исполнителя.
     */
    @Override
    public abstract AbstractOnStoppingExecuteEvent getStoppingExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return Хранитель статистики выполнения Worker-а.
     */
    protected abstract AbstractWorkerStatisticsInfo createStatisticsInfo();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements Worker">

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
            log.info("Runner " + getWorkerName() + " already is running!");
            return;
        }

        synchronized (this) {
            log.info("starting Runner " + getWorkerName());
            if (getRunner() == null) {
                final var startingEvent = getStartingExecuteEvent();
                if (startingEvent != null) {
                    getApplicationEventPublisher().publishEvent(startingEvent);
                }
                createAndStartRunner();
                startRunnerTimerTaskController();
                log.info("Runner " + getWorkerName() + " started");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                if (isRunning()) {
                    this.stoppingExecuteEventCalled = false;
                    log.info("Runner " + getWorkerName() + " started success.");
                    return;
                }
            }
        }
        log.error("Runner " + getWorkerName() + " not started!");
    }

    protected void internalStop() {
        log.info("Starting internalStop()");
        if (!isRunning()) {
            log.info("Runner " + getWorkerName() + " already is stopped!");
            return;
        }

        synchronized (this) {
            internalWaitStop(this.settingsContainer.getWaitOnStopMs());
        }
        log.info("Finished internalStop()");
    }

    /**
     * Ожидание штатного завершения исполнителя в течение заданного времени.
     *
     * @param timeoutMs время в течение которого надо подождать штатного завершения исполнителя.
     */
    @SuppressWarnings("BusyWait")
    private void internalWaitStop(int timeoutMs) {
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

            if (getRunner() != null) {
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
                log.info("Runner " + getWorkerName() + " is stopped success!");
            } else {
                log.info("Runner " + getWorkerName() + " is not stopped!");
            }
            final var stoppingEvent = getStoppingExecuteEvent();
            if (stoppingEvent != null) {
                this.stoppingExecuteEventCalled = true;
                getApplicationEventPublisher().publishEvent(stoppingEvent);
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
                final var stoppingEvent = getStoppingExecuteEvent();
                if (stoppingEvent != null) {
                    this.stoppingExecuteEventCalled = true;
                    getApplicationEventPublisher().publishEvent(stoppingEvent);
                }
            }
        }
    }

    /**
     * Запуск исполнителя
     */
    protected void createAndStartRunner() {
        final var event = this.getIterationExecuteEvent();
        event.setImmediateRunNextIteration(false);
        event.setNeedRestart(false);
        event.setStopExecution(false);
        runnerIsLifeSet();
        new Thread((this.runner = new Runner()), this.workerName).start();
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
            new Thread((this.restartingController = new RestartingController()), this.workerName + "-Restart").start();
        }
    }

    /**
     * Запуск контроллера за зависаниями исполнителя
     */
    protected void startRunnerTimerTaskController() {
        final var timeout = this.settingsContainer.getTimeoutRunnerLifeMs();
        this.runnerTimerTaskController = new RunnerTimerTaskController();
        this.timer = new Timer(getWorkerName() + "-Timer", true);
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
        @SuppressWarnings("BusyWait")
        @SneakyThrows
        @Override
        public void run() {
            this.isStopping.set(false);
            this.currentThread = Thread.currentThread();
            final var event = AbstractWorker.this.getIterationExecuteEvent();

            log.info("Starting run()");
            try {
                if (getRestartingController() != null) {
                    log.info("Waiting release restartingController");
                    while (getRestartingController() != null) {
                        runnerIsLifeSet();
                        Thread.sleep(AbstractWorker.this.settingsContainer.getMinTimePerIterationMs());
                    }
                    log.info("restartingController released!");
                }

                while (!this.isStopping.get()) {
                    event.reset();
                    final var iterationStarted = System.currentTimeMillis();
                    doIteration();
                    if (event.isNeedRestart() || event.isStopExecution()) {
                        log.info("break run(): iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart() + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution());
                        break;
                    }
                    doIdleIfNeed(iterationStarted);
                }
            } finally {
                this.currentThread = null;
                setRunner(null);

                if (event.isStopExecution()) {
                    log.info("Finished run(): setAutoRestart(false)");
                    setAutoRestart(false);
                }
                if (event.isNeedRestart()) {
                    log.info("After finished run(): startRestartingController()");
                    startRestartingController();
                }
            }
        }

        /**
         * Выполняет одну итерацию цикла обработки.
         */
        protected void doIteration() {
            final var stat = getStatisticsInfo();
            stat.iterationStarted();
            log.debug("Starting doIteration()");
            try {
                final var event = AbstractWorker.this.getIterationExecuteEvent();
                runnerIsLifeSet();
                getApplicationEventPublisher().publishEvent(event);
                if (event.isStopExecution()) {
                    this.isStopping.set(true);
                }
                log.debug("Finished doIteration(): iterationExecuteEvent.isStopExecution() == " + event.isStopExecution()
                        + "; iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart());
            } finally {
                // Фиксируем в статистику факт выполнения итерации
                stat.iterationExecuted();
                if (getSettingsContainer().getPrintStatisticsEveryMs() < stat.lastResetMsAgo()) {
                    log.info(stat.getPrintableInfo());
                    stat.reset();
                }
            }
        }

        /**
         * Выполняет необходимый простой, если время выполнения текущей итерации было меньше порогового.
         *
         * @param stepStarted - время начала выполнения текущей итерации
         * @see WorkerSettingsContainer#getMinTimePerIterationMs()
         */
        protected void doIdleIfNeed(long stepStarted) {
            final var event = AbstractWorker.this.getIterationExecuteEvent();
            if (event.isImmediateRunNextIteration()
                    || event.isNeedRestart()
                    || event.isStopExecution()
                    || this.isStopping.get()) {
                log.debug("doIdleIfNeed(): not sleep!");
                return;
            }

            long sleepTime;
            if ((sleepTime = AbstractWorker.this.settingsContainer.getMinTimePerIterationMs() - (System.currentTimeMillis() - stepStarted)) > 0) {
                try {
                    runnerIsLifeSet();
                    log.debug("doIdleIfNeed(): sleep(" + sleepTime + ")!");
                    getStatisticsInfo().sleepStarted();
                    try {
                        Thread.sleep(sleepTime);
                    } finally {
                        getStatisticsInfo().sleepFinished();
                    }
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * Наблюдатель, который завершает (и при необходимости запускает) исполнителя, если тот "завис"
     *
     * @see WorkerSettingsContainer#getTimeoutRunnerLifeMs()
     * @see #lastRunnerLifeCheckedMs
     * @see #autoRestart
     */
    protected class RunnerTimerTaskController extends TimerTask {
        @SneakyThrows
        @Override
        public void run() {
            final var event = AbstractWorker.this.getIterationExecuteEvent();
            final var current = System.currentTimeMillis();
            log.debug("Starting TaskController.run():"
                    + " iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart()
                    + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution());
            if (event.isNeedRestart()
                    || event.isStopExecution()
                    || current - getLastRunnerLifeCheckedMs() > AbstractWorker.this.settingsContainer.getTimeoutRunnerLifeMs()) {
                if (isRunning()) {
                    log.info("Before internalStop(); current == " + current
                            + "; getLastRunnerLifeCheckedMs() == " + getLastRunnerLifeCheckedMs()
                            + "; delta == " + (current - getLastRunnerLifeCheckedMs())
                            + "; iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart()
                            + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution()
                    );
                    internalStop();
                }
                if (isAutoRestart() && !event.isStopExecution()) {
                    startRestartingController();
                }
            }
        }
    }

    /**
     * Класс, который отвечает за процедуру перезапуска.
     */
    protected class RestartingController implements Runnable {
        @SuppressWarnings("BusyWait")
        @SneakyThrows
        @Override
        public void run() {
            final var wait = AbstractWorker.this.settingsContainer.getWaitOnRestartMs();
            final var event = AbstractWorker.this.getIterationExecuteEvent();
            log.info("Restarting... Wait: {} ms", wait);

            event
                    .setImmediateRunNextIteration(false)
                    .setNeedRestart(false)
                    .setStopExecution(false);

            internalStopTimer();
            if (isRunning()) {
                internalStop();
            }
            final var waitTo = System.currentTimeMillis() + AbstractWorker.this.settingsContainer.getWaitOnRestartMs();
            synchronized (AbstractWorker.this) {
                while (System.currentTimeMillis() < waitTo) {
                    log.debug("Restarting... waitLeft: {}", waitTo - System.currentTimeMillis());
                    runnerIsLifeSet();
                    Thread.sleep(AbstractWorker.this.settingsContainer.getTimeoutRunnerLifeMs() / 10);
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

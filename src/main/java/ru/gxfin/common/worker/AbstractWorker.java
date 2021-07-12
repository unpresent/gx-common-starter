package ru.gxfin.common.worker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс исполнителя.<br/>
 * Запускает внутреннего Runner-а в отдельном потоке. Runner внутри себя бросает событие (spring-event),
 * обработчик которого должен содержать главную логику работы итераций. <br/>
 * Также зпускает контроллера-демона, который следит за работой Runner-а, если второй зависает, то демон перезапускает Runner-а.
 *
 * @see AbstractIterationExecuteEvent
 * @see Worker
 */
@Slf4j
public abstract class AbstractWorker implements Worker {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Settings">
    /**
     * ApplicationContext используется для бросания событий stepExecutorEvent (используется spring-events)
     */
    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private ApplicationContext context;

    /**
     * Название исполнителя. Используется в логировании.
     */
    @Getter
    private final String name;

    /**
     * Признак того, что исполнителя требуется перезапускать автоматически, если он остановлен по каким-либо причинам
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private volatile boolean autoRestart;

    /**
     * Внутренний исполнитель - работает в отдельном потоке. Содержит в себе цикл до сигнала выхода.
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private volatile Runner runner;

    /**
     * Контроллер, который следит за тем, что исполнитель "живой" ("не завис")
     */
    @Getter(AccessLevel.PROTECTED)
    private volatile RunnerTimerTaskController runnerTimerTaskController;

    /**
     * Таймер, который запускает периодически Контролера за Исполнителем
     */
    @Getter(AccessLevel.PROTECTED)
    private volatile Timer timer;

    @Getter(AccessLevel.PROTECTED)
    private volatile RestartingController restartingController = null;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути должен содержать логику итераций
     */
    @Getter(AccessLevel.PROTECTED)
    private final AbstractIterationExecuteEvent iterationExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван перед запуском Исполнителя.
     */
    @Getter(AccessLevel.PROTECTED)
    private final AbstractStartingExecuteEvent startingExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван после останова Исполнителя.
     */
    @Getter(AccessLevel.PROTECTED)
    private final AbstractStoppingExecuteEvent stoppingExecuteEvent;

    /**
     * Признак того, что событие об остнове Исполнителя уже вызывалось.
     * Требуется для разового вызова.
     * При останове исполнителя устанавливается в true.
     * При запуске Исполнителя сбрасывается в false.
     */
    @Getter(AccessLevel.PROTECTED)
    private boolean stoppingExecuteEventCalled = false;

    /**
     * Минимальное время на итерацию. Если после выполнения итерации не требуется немедленно продолжить,
     * и время выполнения текущей итерации было менее указанного, то поток исполнителя заспает
     */
    protected abstract int getMinTimePerIterationMs();

    /**
     * Момент времени, когда Runner последний раз отчитывался, что работает.
     */
    @Getter
    private volatile long lastRunnerLifeCheckedMs = 0;

    /**
     * Метод, с помощью которого исполнитель отчитыватся, что еще "жив"
     *
     * @see #lastRunnerLifeCheckedMs
     * @see #getTimoutRunnerLifeMs()
     */
    protected void runnerIsLifeSet() {
        this.lastRunnerLifeCheckedMs = System.currentTimeMillis();
    }

    /**
     * Настрйока (в мс), которая определяет период времени, в течение которого исполнитель обязан отчитаться о своей "жизни".
     * Если исполнитель не отчитается, то его требуется остановить (и при необходимости запустить снова).
     *
     * @see #runnerIsLifeSet()
     * @see #lastRunnerLifeCheckedMs
     * @see #autoRestart
     */
    protected abstract int getTimoutRunnerLifeMs();

    /**
     * Настрйока (в мс), которая определяет сколько можно ждать штатного завершения исполнителя во время stop().
     *
     * @see #internalStop()
     * @see #internalWaitStop(int, boolean)
     */
    @Override
    public abstract int getWaitOnStopMS();

    /**
     * Настрйока (в мс), которая определяет какую паузу надо выждать перед перезапуском после останова.
     *
     * @see RunnerTimerTaskController
     */
    @Override
    public abstract int getWaitOnRestartMS();

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Init">
    protected AbstractWorker(String name) {
        super();
        this.name = name;
        this.iterationExecuteEvent = createIterationExecuteEvent();
        this.startingExecuteEvent = createStartingExecuteEvent();
        this.stoppingExecuteEvent = createStoppingExecuteEvent();
    }

    /**
     * Требуется переопределить в наледнике.
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    protected abstract AbstractIterationExecuteEvent createIterationExecuteEvent();

    /**
     * Требуется переопределить в наледнике.
     * @return объект-событие, которое будет использоваться для вызова при запуске Исполнителя.
     */
    protected abstract AbstractStartingExecuteEvent createStartingExecuteEvent();

    /**
     * Требуется переопределить в наледнике.
     * @return объект-событие, которое будет использоваться для вызова при останове Исполнителя.
     */
    protected abstract AbstractStoppingExecuteEvent createStoppingExecuteEvent();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements Worker">

    /**
     * Запуск исполнителя.
     */
    @Override
    public final void start() {
        log.info("Starting start()");
        try {
            this.autoRestart = true;
            internalStart(false);
        } finally {
            log.info("Finished start()");
        }
    }

    /**
     * Принудительный останов после ожидания штатного завершения.
     */
    @Override
    public final void stop() {
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
    protected void internalStart(boolean isRestart) {
        if (isRunning()) {
            log.info("Runner " + getName() + " already is running!");
            return;
        }

        synchronized (this) {
            log.info("starting Runner " + getName());
            if (getRunner() == null) {
                if (getStartingExecuteEvent() != null) {
                    getContext().publishEvent(getStartingExecuteEvent());
                }
                createAndStartRunner();
                startRunnerTimerTaskController();
                log.info("Runner " + getName() + " started");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
                }
                if (isRunning()) {
                    this.stoppingExecuteEventCalled = false;
                    log.info("Runner " + getName() + " started success.");
                    return;
                }
            }
        }
        log.error("Runner " + getName() + " not started!");
    }

    protected void internalStop() {
        log.info("Starting internalStop()");
        if (!isRunning()) {
            log.info("Runner " + getName() + " already is stopped!");
            return;
        }

        synchronized (this) {
            internalWaitStop(getWaitOnStopMS(), true);
        }
        log.info("Finished internalStop()");
    }

    /**
     * Ожидание штатного завершения исполнителя в течение заданного времени.
     *
     * @param timeoutMs     время в течение которого надо подождать штатного завершения исполнителя.
     * @param withInterrupt треуется ли прервать выполнение исполнителя, если он не завершился за отведенное время.
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
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
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
                log.info("Runner " + getName() + " is stopped success!");
            } else {
                log.info("Runner " + getName() + " is not stopped!");
            }
            if (getStoppingExecuteEvent() != null) {
                this.stoppingExecuteEventCalled = true;
                getContext().publishEvent(getStoppingExecuteEvent());
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
                    getContext().publishEvent(getStoppingExecuteEvent());
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
        new Thread((this.runner = new Runner()), this.name).start();
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
            new Thread((this.restartingController = new RestartingController()), this.name + "-Restart").start();
        }
    }

    /**
     * Запуск контроллера за зависаниями исполнителя
     */
    protected void startRunnerTimerTaskController() {
        final var timeout = getTimoutRunnerLifeMs();
        this.runnerTimerTaskController = new RunnerTimerTaskController();
        this.timer = new Timer(getName() + "-Timer", true);
        this.timer.scheduleAtFixedRate(this.runnerTimerTaskController, timeout, timeout / 10);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Исполнитель. run() работает в своем потоке.
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
         * @return true - требуется продолжать обработку. false - требуется остановить обработку.
         */
        protected void doIteration() {
            log.debug("Starting doStep()");
            runnerIsLifeSet();
            getContext().publishEvent(iterationExecuteEvent);
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
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
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
            log.info("Starting TaskController.run():"
                    + " iterationExecuteEvent.isNeedRestart() == " + iterationExecuteEvent.isNeedRestart()
                    + "; iterationExecuteEvent.isStopExecution() == " + iterationExecuteEvent.isStopExecution()
            );
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
            final var wait = getWaitOnRestartMS();
            log.info("Restarting... Wait: {} ms", wait);
            AbstractWorker.this.iterationExecuteEvent.setImmediateRunNextIteration(false);
            AbstractWorker.this.iterationExecuteEvent.setNeedRestart(false);
            AbstractWorker.this.iterationExecuteEvent.setStopExecution(false);

            internalStopTimer();
            if (isRunning()) {
                internalStop();
            }
            final var waitTo = System.currentTimeMillis() + getWaitOnRestartMS();
            synchronized (AbstractWorker.this) {
                while (System.currentTimeMillis() < waitTo) {
                    log.info("Restarting... waitLeft: {}", waitTo - System.currentTimeMillis());
                    runnerIsLifeSet();
                    Thread.sleep(getTimoutRunnerLifeMs() / 10);
                }
            }
            if (!isRunning()) {
                internalStart(true);
            }
            restartingController = null;
            log.info("Restarting finished!");
        }
    }
}

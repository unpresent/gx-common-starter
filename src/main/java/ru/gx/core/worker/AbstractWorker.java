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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PROTECTED;

/**
 * Класс Исполнителя.<br/>
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
    /**
     * Монитор для исполнителя
     */
    private final Object executingMonitor = new Object();

    /**
     * Монитор для механизма перезапуска
     */
    private final Object restartingMonitor = new Object();

    /**
     * Реестр метрик
     */
    @Getter(PROTECTED)
    @NotNull
    private final MeterRegistry meterRegistry;

    /**
     * Название исполнителя. Используется в логировании.
     */
    @Getter
    @NotNull
    private final String workerName;

    /**
     * Контейнер настроек для данного Исполнителя
     */
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

    private final ExecutorService executorService;

    /**
     * Внутренний исполнитель - работает в отдельном потоке. Содержит в себе цикл до сигнала выхода.
     */
    private final AtomicReference<Runner> runner = new AtomicReference<>();

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

    /**
     * Контролер перезапуска основного потока Исполнителя
     */
    @Getter(PROTECTED)
    private volatile RestartingController restartingController = null;

    /**
     * Признак того, что событие об остановке Исполнителя уже вызывалось.
     * Требуется для разового вызова.
     * При останове исполнителя устанавливается в true.
     * При запуске Исполнителя сбрасывается в false.
     */
    @Getter(PROTECTED)
    private volatile boolean stoppingExecuteEventCalled = false;

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
     * Текущий исполняемый шаг. Устанавливается в различных местах исполнителя,
     * чтобы перезапуске/остановке вывести в лог.
     */
    @Getter
    @Setter(PROTECTED)
    private String currentExecutionInfo;

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
            @NotNull final ApplicationEventPublisher applicationEventPublisher
    ) {
        this.workerName = workerName;
        this.settingsContainer = settingsContainer;
        this.meterRegistry = meterRegistry;
        this.applicationEventPublisher = applicationEventPublisher;
        this.statisticsInfo = createStatisticsInfo();
        this.executorService = Executors.newSingleThreadExecutor();
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
    protected Runner getRunner() {
        return this.runner.get();
    }

    protected boolean setRunner(@NotNull final Runner runner) {
        return this.runner.compareAndSet(null, runner);
    }

    protected void clearRunner() {
        final var runner = getRunner();
        if (runner != null) {
            runner.currentThread = null;
        }
        this.runner.set(null);
    }

    protected void internalStart() {
        if (isRunning()) {
            log.info("Runner {} of worker " + getWorkerName() + " already is running!", getRunner());
            return;
        }

        synchronized (this.restartingMonitor) {
            log.info("Starting Runner of worker " + getWorkerName());
            if (getRunner() == null) {
                final var startingEvent = getStartingExecuteEvent();
                if (startingEvent != null) {
                    getApplicationEventPublisher().publishEvent(startingEvent);
                }
                createAndStartRunner();
                startRunnerTimerTaskController();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                if (isRunning()) {
                    this.stoppingExecuteEventCalled = false;
                    log.info("Runner {} of worker " + getWorkerName() + " started success!", getRunner());
                    return;
                }
            }
        }
        log.error("Runner " + getWorkerName() + " not started!");
    }

    protected void internalStop() {
        log.info("START internalStop(); currentExecutionInfo: {}", getCurrentExecutionInfo());
        if (!isRunning()) {
            log.info("Runner " + getWorkerName() + " already is stopped!");
            return;
        }

        synchronized (this.restartingMonitor) {
            internalWaitStop(this.settingsContainer.getWaitOnStopMs());
        }
        log.info("FINISH internalStop(); currentExecutionInfo: {}", getCurrentExecutionInfo());
    }

    /**
     * Ожидание штатного завершения исполнителя в течение заданного времени.
     *
     * @param timeoutMs время в течение которого надо подождать штатного завершения исполнителя.
     */
    private void internalWaitStop(int timeoutMs) {
        var runner = getRunner();
        if (runner == null) {
            return;
        }
        runner.isStopping.set(true);
        try {
            if (!this.executorService.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
                // Прерываем
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("", e);
        } finally {
            clearRunner();
            internalStopTimer();
            log.info("Runner " + getWorkerName() + " is stopped!");
            final var stoppingEvent = getStoppingExecuteEvent();
            if (stoppingEvent != null) {
                this.stoppingExecuteEventCalled = true;
                getApplicationEventPublisher()
                        .publishEvent(stoppingEvent);
            }
        }
    }

    /**
     * Останов timer-а
     */
    protected void internalStopTimer() {
        AbstractWorker.this.runnerTimerTaskController = null;
        if (AbstractWorker.this.timer != null) {
            synchronized (this.restartingMonitor) {
                log.info("Stopping timer...");
                final var timer = AbstractWorker.this.timer;
                if (timer != null) {
                    AbstractWorker.this.timer = null;
                    timer.cancel();
                }
                log.info("Timer stopped.");
            }
        }
    }

    /**
     * Запуск исполнителя
     */
    protected void createAndStartRunner() {
        log.info("START createAndStartRunner()");
        final var event = this.getIterationExecuteEvent();
        event.setImmediateRunNextIteration(false);
        event.setNeedRestart(false);
        event.setStopExecution(false);
        runnerIsLifeSet();
        final var runner = new Runner();
        if (!setRunner(runner)) {
            throw new RuntimeException(String.format("Can't set new runner %s because current value is not null", runner));
        }
        // new Thread(getRunner(), this.workerName).start();
        this.executorService.execute(runner);
        log.info("FINISH createAndStartRunner(); runner = {}", runner);
    }

    /**
     * Запуск потока перезапуска.
     */
    protected void startRestartingController() {
        if (getRestartingController() != null) {
            return;
        }
        synchronized (this.restartingMonitor) {
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

        @Override
        public String toString() {
            if (currentThread != null) {
                return super.toString() + " {currentThread: name = " + currentThread.getName() + "; id = " + currentThread.getId() + "}";
            } else {
                return super.toString();
            }
        }

        /**
         * Метод run с основным бесконечным циклом работы исполнителя
         */
        @SuppressWarnings("BusyWait")
        @SneakyThrows
        @Override
        public void run() {
            this.isStopping.set(false);
            this.currentThread = Thread.currentThread();
            this.currentThread.setName(getWorkerName());
            final var event = AbstractWorker.this.getIterationExecuteEvent();

            log.info("START run(); runner = " + this);
            try {
                if (getRestartingController() != null) {
                    log.info("Waiting release restartingController");
                    while (getRestartingController() != null) {
                        runnerIsLifeSet();
                        Thread.sleep(AbstractWorker.this.settingsContainer.getMinTimePerIterationMs());
                    }
                    log.info("restartingController released!");
                }

                while (!this.isStopping.get() && this.currentThread != null && !this.currentThread.isInterrupted()) {
                    event.reset();
                    final var iterationStarted = System.currentTimeMillis();
                    doIteration();
                    if (event.isNeedRestart() || event.isStopExecution()) {
                        log.info("BREAK run(): iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart() + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution());
                        break;
                    }
                    doIdleIfNeed(iterationStarted);
                }
            } finally {
                log.info("FINISH run(); runner = {}", this);
                this.currentThread = null;
                clearRunner();

                if (event.isStopExecution()) {
                    log.info("FINISH run(): setAutoRestart(false); runner = {}", this);
                    setAutoRestart(false);
                }
                if (event.isNeedRestart()) {
                    log.info("AFTER FINISH run(): startRestartingController(); runner = {}", this);
                    startRestartingController();
                }
            }
        }

        /**
         * Выполняет одну итерацию цикла обработки.
         */
        protected void doIteration() {
            if (this.isStopping.get() || this.currentThread == null || this.currentThread.isInterrupted()) {
                return;
            }

            synchronized (AbstractWorker.this.executingMonitor) {
                final var stat = getStatisticsInfo();
                stat.iterationStarted();
                log.debug("START doIteration()");
                try {
                    final var event = AbstractWorker.this.getIterationExecuteEvent();
                    runnerIsLifeSet();
                    setCurrentExecutionInfo("Starting event: " + event);
                    getApplicationEventPublisher().publishEvent(event);
                    runnerIsLifeSet();
                    setCurrentExecutionInfo("Finished event: " + event);
                    if (event.isStopExecution()) {
                        this.isStopping.set(true);
                    }
                    log.debug("FINISH doIteration(): iterationExecuteEvent.isStopExecution() == " + event.isStopExecution()
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
        }

        /**
         * Выполняет необходимый простой, если время выполнения текущей итерации было меньше порогового.
         *
         * @param stepStarted - время начала выполнения текущей итерации
         * @see WorkerSettingsContainer#getMinTimePerIterationMs()
         */
        protected void doIdleIfNeed(long stepStarted) {
            if (this.isStopping.get() || this.currentThread == null || this.currentThread.isInterrupted()) {
                return;
            }

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
                    final var info = "Before doIdleIfNeed(): sleep(" + sleepTime + ")!";
                    setCurrentExecutionInfo(info);
                    log.debug(info);
                    getStatisticsInfo().sleepStarted();
                    try {
                        Thread.sleep(sleepTime);
                    } finally {
                        getStatisticsInfo().sleepFinished();
                    }
                    setCurrentExecutionInfo("After sleep");
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
            log.debug("START RunnerTimerTaskController.run():"
                    + " iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart()
                    + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution());
            if (event.isNeedRestart()
                    || event.isStopExecution()
                    || current - getLastRunnerLifeCheckedMs() > AbstractWorker.this.settingsContainer.getTimeoutRunnerLifeMs()) {
                if (isRunning()) {
                    log.info("CALL internalStop(); current == " + current
                            + "; getLastRunnerLifeCheckedMs() == " + getLastRunnerLifeCheckedMs()
                            + "; delta == " + (current - getLastRunnerLifeCheckedMs())
                            + "; iterationExecuteEvent.isNeedRestart() == " + event.isNeedRestart()
                            + "; iterationExecuteEvent.isStopExecution() == " + event.isStopExecution()
                            + "; currentExecutionInfo:= " + AbstractWorker.this.getCurrentExecutionInfo()
                    );
                    internalStop();
                }
                if (isAutoRestart() && !event.isStopExecution()) {
                    startRestartingController();
                }
            }
            log.debug("FINISH RunnerTimerTaskController.run()");
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
            synchronized (AbstractWorker.this.restartingMonitor) {
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

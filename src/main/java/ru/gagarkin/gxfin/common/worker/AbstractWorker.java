package ru.gagarkin.gxfin.common.worker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
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

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути должен содержать логику итераций
     */
    @Getter(AccessLevel.PROTECTED)
    private final AbstractIterationExecuteEvent iterationExecuteEvent;

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

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Init">
    protected AbstractWorker(String name) {
        super();
        this.name = name;
        this.iterationExecuteEvent = createIterationExecuteEvent();
        this.runnerTimerTaskController = new RunnerTimerTaskController();
    }

    /**
     * Требуется переопределить в наледнике.
     *
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    protected abstract AbstractIterationExecuteEvent createIterationExecuteEvent();
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
        var runner = getRunner();
        return runner != null && runner.currentThread != null;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Internal methods for implements Worker">
    protected void internalStart() {
        if (isRunning()) {
            log.info("Runner " + getName() + " already is running!");
            return;
        }

        synchronized (this) {
            log.info("starting Runner " + getName());
            if (getRunner() == null) {
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
        var startWait = System.currentTimeMillis();
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
                var thread = runner.currentThread;
                runner.currentThread = null;
                setRunner(null);
                if (thread != null) {
                    thread.interrupt();
                }
            }
        } finally {
            if (!isRunning()) {
                if (this.timer != null) {
                    this.timer.cancel();
                    this.timer = null;
                }
                log.info("Runner " + getName() + " is stopped success!");
            } else {
                log.info("Runner " + getName() + " is not stopped!");
            }
        }
    }

    /**
     * Запуск исполнителя
     */
    protected void createAndStartRunner() {
        this.iterationExecuteEvent.setImmediateRunNextIteration(false);
        this.iterationExecuteEvent.setStopExecution(false);
        runnerIsLifeSet();
        new Thread((this.runner = new Runner())).start();
    }

    /**
     * Запуск контроллера за зависаниями исполнителя
     */
    protected void startRunnerTimerTaskController() {
        var timeout = getTimoutRunnerLifeMs();
        this.timer = new Timer(true);
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
        @Override
        public void run() {
            this.isStopping.set(false);
            this.currentThread = Thread.currentThread();
            log.info("Starting run()");
            try {
                while (!this.isStopping.get()) {
                    iterationExecuteEvent.reset();
                    var stepStarted = System.currentTimeMillis();

                    doStep();
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
                    log.info("After finished run(): createAndStartRunner()");
                    createAndStartRunner();
                }
            }
        }

        /**
         * Выполняет одну итерацию цикла обработки.
         *
         * @return true - требуется продолжать обработку. false - требуется остановить обработку.
         */
        protected void doStep() {
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
        @Override
        public void run() {
            var current = System.currentTimeMillis();
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
                if (isAutoRestart()
                        && !iterationExecuteEvent.isStopExecution()) {
                    log.info("Before internalStart()");
                    internalStart();
                }
            }
        }
    }
}

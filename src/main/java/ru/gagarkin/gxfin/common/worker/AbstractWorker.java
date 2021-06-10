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
 * @see AbstractIterationExecutorEvent
 * @see Worker
 */
@Slf4j
public abstract class AbstractWorker implements Worker {
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
    private final AbstractIterationExecutorEvent iterationExecutorEvent;

    /**
     * Минимальное время на итерацию. Если после выполнения итерации не требуется немедленно продолжить,
     * и время выполнения текущей итерации было менее указанного, то поток исполнителя заспает
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int minTimePerStepMs;

    /**
     * Момент времени, когда Runner последний раз отчитывался, что работает.
     */
    @Getter
    private volatile long lastRunnerLifeCheckedMs;

    /**
     * Метод, с помощью которого исполнитель отчитыватся, что еще "жив"
     *
     * @see #lastRunnerLifeCheckedMs
     * @see #timoutRunnerLifeMs
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
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int timoutRunnerLifeMs;

    /**
     * Настрйока (в мс), которая определяет сколько можно ждать штатного завершения исполнителя при перезапуске.
     * Передается в качестве параметра в процедуру завершения.
     * @see #terminate(int)
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private int waitStopOnRestart;

    protected AbstractWorker(String name, AbstractIterationExecutorEvent iterationExecutorEvent) {
        super();
        this.name = name;
        this.iterationExecutorEvent = iterationExecutorEvent;
        this.runnerTimerTaskController = new RunnerTimerTaskController();
        this.timer = new Timer(true);
    }

    /**
     * Запуск исполнителя
     * @param autoRestart true - требуется автоперезапуск исполнителя при аварийном завершении
     * @return true - запуск успешен
     */
    @Override
    public boolean start(boolean autoRestart) {
        this.autoRestart = autoRestart;

        if (isRunning()) {
            log.info("Runner " + getName() + " already is running!");
            return isRunning();
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
                    return true;
                }
            }
        }
        log.error("Runner " + getName() + " not started!");
        return false;
    }

    @Override
    public boolean tryStop(int timeoutMs) {
        if (!isRunning()) {
            log.info("Runner " + getName() + " already is stopped!");
            return true;
        }

        synchronized (this) {
            if (getRunner() != null) {
                internalWaitStop(timeoutMs, false);
            }

            if (!isRunning()) {
                getTimer().cancel();
                log.info("Runner " + getName() + " is stopped success!");
                return true;
            } else {
                log.info("Runner " + getName() + " is not stopped!");
                return false;
            }
        }
    }

    @Override
    public void terminate(int timeoutMs) {
        if (!isRunning()) {
            log.info("Runner " + getName() + " already is stopped!");
            return;
        }

        synchronized (this) {
            internalWaitStop(timeoutMs, true);
            if (getRunner() != null) {
                var thread = getRunner().currentThread;
                if (thread != null) {
                    thread.interrupt();
                }
            }

            if (!isRunning()) {
                getTimer().cancel();
                log.info("Runner " + getName() + " is stopped success!");
            } else {
                log.info("Runner " + getName() + " is not stopped!");
            }
        }
    }

    private void internalWaitStop(int timeoutMs, boolean withInterrupt) {
        var runner = getRunner();
        if (runner == null) {
            return;
        }
        runner.isStopping.set(true);
        while (getRunner() != null) {
            try {
                Thread.sleep(timeoutMs / 10);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                log.error(e.getStackTrace().toString());
            }
        }

        if (withInterrupt && getRunner() != null) {
            var thread = getRunner().currentThread;
            if (thread != null) {
                thread.interrupt();
            }
        }

        if (!isRunning()) {
            getTimer().cancel();
            log.info("Runner " + getName() + " is stopped success!");
        } else {
            log.info("Runner " + getName() + " is not stopped!");
        }
    }

    protected void createAndStartRunner() {
        runnerIsLifeSet();
        this.runner = new Runner();
        new Thread(runner).start();
    }

    protected void startRunnerTimerTaskController() {
        this.timer.cancel();
        this.timer.scheduleAtFixedRate(this.runnerTimerTaskController, timoutRunnerLifeMs, timoutRunnerLifeMs / 10);
    }

    /**
     * Текущий статус исполнителя
     * @return true = исполнитель запущен и работает
     */
    @Override
    public boolean isRunning() {
        var runner = getRunner();
        return runner != null && runner.currentThread != null;
    }

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
            try {
                while (!this.isStopping.get()) {
                    var stepStarted = System.currentTimeMillis();
                    doStep();
                    doIdleIfNeed(stepStarted);
                }
            } finally {
                this.currentThread = null;
                setRunner(null);
            }
        }

        /**
         * Выполняет одну итерацию цикла обработки.
         *
         * @return true - требуется продолжать обработку. false - требуется остановить обработку.
         */
        protected void doStep() {
            runnerIsLifeSet();
            getContext().publishEvent(iterationExecutorEvent);
            if (iterationExecutorEvent.isStopExecution()) {
                this.isStopping.set(true);
            }
        }

        /**
         * Выполняет необходимый простой, если время выполнения текущей итерации было меньше порогового.
         *
         * @param stepStarted - время начала выполнения текущей итерации
         * @see #minTimePerStepMs
         */
        protected void doIdleIfNeed(long stepStarted) {
            if (iterationExecutorEvent.isImmediateRunNextIteration()
                    || iterationExecutorEvent.isStopExecution()
                    || this.isStopping.get()) {
                return;
            }

            long sleepTime;
            if ((sleepTime = System.currentTimeMillis() - stepStarted) > minTimePerStepMs) {
                try {
                    runnerIsLifeSet();
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
     * @see #timoutRunnerLifeMs
     * @see #lastRunnerLifeCheckedMs
     * @see #autoRestart
     */
    protected class RunnerTimerTaskController extends TimerTask {
        @Override
        public void run() {
            if (!isRunning()) {
                return;
            }
            var current = System.currentTimeMillis();
            if (current - getLastRunnerLifeCheckedMs() > getTimoutRunnerLifeMs()) {
                terminate(getWaitStopOnRestart());
                if (isAutoRestart()) {
                    start(true);
                }
            }
        }
    }
}

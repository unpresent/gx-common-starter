package ru.gx.worker;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public static final String SIMPLE_WORKER_NAME = "simple-worker";

    public SimpleWorker() {
        super(SIMPLE_WORKER_NAME);
    }

    @Override
    public AbstractOnIterationExecuteEvent iterationExecuteEvent() {
        return getIterationExecuteEvent().reset();
    }

    @Override
    public AbstractOnStartingExecuteEvent startingExecuteEvent() {
        return getStartingExecuteEvent().reset();
    }

    @Override
    public AbstractOnStoppingExecuteEvent stoppingExecuteEvent() {
        return getStoppingExecuteEvent().reset();
    }

    @Override
    public void runnerIsLifeSet() {
        super.runnerIsLifeSet();
    }
}

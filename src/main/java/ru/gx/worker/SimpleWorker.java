package ru.gx.worker;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public static final String WORKER_DEFAULT_NAME = "simple-worker";

    public SimpleWorker(@NotNull final String name) {
        super(name);
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

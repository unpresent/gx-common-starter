package ru.gx.worker;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public SimpleWorker(@NotNull String serviceName) {
        super(serviceName);
    }

    @Override
    protected AbstractIterationExecuteEvent createIterationExecuteEvent() {
        return new SimpleIterationExecuteEvent(this);
    }

    @Override
    protected AbstractStartingExecuteEvent createStartingExecuteEvent() {
        return new SimpleStartingExecuteEvent(this);
    }

    @Override
    protected AbstractStoppingExecuteEvent createStoppingExecuteEvent() {
        return new SimpleStoppingExecuteEvent(this);
    }

    @Override
    public void runnerIsLifeSet() {
        super.runnerIsLifeSet();
    }
}

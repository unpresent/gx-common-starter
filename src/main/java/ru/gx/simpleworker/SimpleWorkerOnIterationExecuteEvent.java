package ru.gx.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractOnIterationExecuteEvent;

public class SimpleWorkerOnIterationExecuteEvent extends AbstractOnIterationExecuteEvent {
    public SimpleWorkerOnIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

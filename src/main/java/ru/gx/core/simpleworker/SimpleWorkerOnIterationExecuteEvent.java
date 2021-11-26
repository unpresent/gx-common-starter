package ru.gx.core.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnIterationExecuteEvent;

public class SimpleWorkerOnIterationExecuteEvent extends AbstractOnIterationExecuteEvent {
    public SimpleWorkerOnIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

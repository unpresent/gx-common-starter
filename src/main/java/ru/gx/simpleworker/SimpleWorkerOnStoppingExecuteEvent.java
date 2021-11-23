package ru.gx.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractOnStoppingExecuteEvent;

public class SimpleWorkerOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public SimpleWorkerOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

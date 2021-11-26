package ru.gx.core.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStoppingExecuteEvent;

public class SimpleWorkerOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public SimpleWorkerOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

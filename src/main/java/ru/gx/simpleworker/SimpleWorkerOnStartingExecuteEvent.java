package ru.gx.simpleworker;

import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractOnStartingExecuteEvent;

public class SimpleWorkerOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public SimpleWorkerOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

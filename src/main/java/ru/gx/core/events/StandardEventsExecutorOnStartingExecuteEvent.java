package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStartingExecuteEvent;

public class StandardEventsExecutorOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public StandardEventsExecutorOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

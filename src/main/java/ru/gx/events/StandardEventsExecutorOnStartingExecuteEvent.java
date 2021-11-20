package ru.gx.events;

import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractOnStartingExecuteEvent;

public class StandardEventsExecutorOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public StandardEventsExecutorOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

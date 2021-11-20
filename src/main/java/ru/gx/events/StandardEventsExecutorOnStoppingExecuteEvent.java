package ru.gx.events;

import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractOnStoppingExecuteEvent;

public class StandardEventsExecutorOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public StandardEventsExecutorOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStoppingExecuteEvent;

public class StandardEventsExecutorOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public StandardEventsExecutorOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

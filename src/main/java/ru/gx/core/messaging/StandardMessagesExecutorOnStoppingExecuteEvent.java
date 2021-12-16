package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStoppingExecuteEvent;

public class StandardMessagesExecutorOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public StandardMessagesExecutorOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

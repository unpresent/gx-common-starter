package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractOnStartingExecuteEvent;

public class StandardMessagesExecutorOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public StandardMessagesExecutorOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

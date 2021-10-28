package ru.gx.worker;

import org.jetbrains.annotations.NotNull;

public class SimpleOnStartingExecuteEvent extends AbstractOnStartingExecuteEvent {
    public SimpleOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

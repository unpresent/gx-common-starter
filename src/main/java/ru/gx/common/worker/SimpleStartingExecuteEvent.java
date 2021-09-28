package ru.gx.common.worker;

import org.jetbrains.annotations.NotNull;

public class SimpleStartingExecuteEvent extends AbstractStartingExecuteEvent {
    public SimpleStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

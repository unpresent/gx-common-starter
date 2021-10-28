package ru.gx.worker;

import org.jetbrains.annotations.NotNull;

public class SimpleOnIterationExecuteEvent extends AbstractOnIterationExecuteEvent {
    public SimpleOnIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

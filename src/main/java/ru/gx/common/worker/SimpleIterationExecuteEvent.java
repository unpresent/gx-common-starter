package ru.gx.common.worker;

import org.jetbrains.annotations.NotNull;

public class SimpleIterationExecuteEvent extends AbstractIterationExecuteEvent {
    public SimpleIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

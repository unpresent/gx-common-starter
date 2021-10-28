package ru.gx.worker;

import org.jetbrains.annotations.NotNull;

public class SimpleOnStoppingExecuteEvent extends AbstractOnStoppingExecuteEvent {
    public SimpleOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

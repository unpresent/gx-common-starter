package ru.gx.common.worker;

import org.springframework.context.ApplicationEvent;

public class AbstractStoppingExecuteEvent extends ApplicationEvent {
    public AbstractStoppingExecuteEvent(Object source) {
        super(source);
    }
}

package ru.gxfin.common.worker;

import org.springframework.context.ApplicationEvent;

public class AbstractStoppingExecuteEvent extends ApplicationEvent {
    public AbstractStoppingExecuteEvent(Object source) {
        super(source);
    }
}

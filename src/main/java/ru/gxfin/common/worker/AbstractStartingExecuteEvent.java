package ru.gxfin.common.worker;

import org.springframework.context.ApplicationEvent;

public class AbstractStartingExecuteEvent extends ApplicationEvent {
    public AbstractStartingExecuteEvent(Object source) {
        super(source);
    }
}

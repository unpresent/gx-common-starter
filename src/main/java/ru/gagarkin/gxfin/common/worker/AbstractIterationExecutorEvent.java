package ru.gagarkin.gxfin.common.worker;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractIterationExecutorEvent extends ApplicationEvent {
    @Getter
    @Setter
    private boolean stopExecution = false;

    @Getter
    @Setter
    private boolean immediateRunNextIteration;

    public AbstractIterationExecutorEvent(Object source) {
        super(source);
    }
}

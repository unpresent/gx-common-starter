package ru.gagarkin.gxfin.common.worker;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Объект-событие.<br/>
 * Используется для передачи управления обработчику итерации исполнителя.
 */
public abstract class AbstractIterationExecuteEvent extends ApplicationEvent {
    /**
     * Признак того, требуется ли перезапустить работу Исполнителя.
     * @see Worker
     * @see AbstractWorker
     */
    @Getter
    @Setter
    private volatile boolean needRestart = false;

    /**
     * Признак того, требуется ли остановить работу Исполнителя.
     * @see Worker
     * @see AbstractWorker
     */
    @Getter
    @Setter
    private volatile boolean stopExecution = false;

    /**
     * Требуется выполнить следующую итерацию, без пауз.
     * @see Worker
     * @see AbstractWorker
     */
    @Getter
    @Setter
    private boolean immediateRunNextIteration;

    public AbstractIterationExecuteEvent(Object source) {
        super(source);
    }

    public void reset() {
        setNeedRestart(false);
        setStopExecution(false);
        setImmediateRunNextIteration(false);
    }
}

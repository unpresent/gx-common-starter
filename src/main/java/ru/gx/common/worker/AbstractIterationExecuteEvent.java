package ru.gx.common.worker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

/**
 * Объект-событие.<br/>
 * Используется для передачи управления обработчику итерации исполнителя.
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractIterationExecuteEvent extends ApplicationEvent {
    /**
     * Признак того, требуется ли перезапустить работу Исполнителя.
     * @see Worker
     * @see AbstractWorker
     */
    private volatile boolean needRestart = false;

    /**
     * Признак того, требуется ли остановить работу Исполнителя.
     * @see Worker
     * @see AbstractWorker
     */
    private volatile boolean stopExecution = false;

    /**
     * Требуется выполнить следующую итерацию, без пауз.
     * @see Worker
     * @see AbstractWorker
     */
    private boolean immediateRunNextIteration;

    protected AbstractIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }

    public void reset() {
        this
                .setNeedRestart(false)
                .setStopExecution(false)
                .setImmediateRunNextIteration(false);
    }
}

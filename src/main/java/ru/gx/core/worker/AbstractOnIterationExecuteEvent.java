package ru.gx.core.worker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление каждую итерацию цикла Worker-а.
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractOnIterationExecuteEvent extends ApplicationEvent implements OnIterationExecuteEvent {
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

    protected AbstractOnIterationExecuteEvent(@NotNull final Object source) {
        super(source);
    }

    @Override
    public AbstractOnIterationExecuteEvent reset() {
        this
                .setNeedRestart(false)
                .setStopExecution(false)
                .setImmediateRunNextIteration(false);
        return this;
    }
}

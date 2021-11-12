package ru.gx.worker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

/**
 * Объект-событие.<br/>
 * Публикация данного события останавливает Worker.
 * Слушателем данного является сам Worker.
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractDoStopWorkerEvent extends ApplicationEvent {
    protected AbstractDoStopWorkerEvent(@NotNull final Object source) {
        super(source);
    }
}
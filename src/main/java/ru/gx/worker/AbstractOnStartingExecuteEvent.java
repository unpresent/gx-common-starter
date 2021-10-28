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
 * Слушатель данного события получает управление перед запуском цикла Worker-а (при запуске и/или перезапуске).
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AbstractOnStartingExecuteEvent extends ApplicationEvent {
    protected AbstractOnStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }

    public AbstractOnStartingExecuteEvent reset() {
        return this;
    }
}

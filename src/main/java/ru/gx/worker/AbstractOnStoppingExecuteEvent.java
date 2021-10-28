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
 * Слушатель данного события получает управление после окончания цикла Worker-а (при останове, перед перезапуском).
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AbstractOnStoppingExecuteEvent extends ApplicationEvent {
    protected AbstractOnStoppingExecuteEvent(@NotNull final Object source) {
        super(source);
    }

    public AbstractOnStoppingExecuteEvent reset() {
        return this;
    }
}

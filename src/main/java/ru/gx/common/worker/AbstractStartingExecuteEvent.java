package ru.gx.common.worker;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class AbstractStartingExecuteEvent extends ApplicationEvent {
    protected AbstractStartingExecuteEvent(@NotNull final Object source) {
        super(source);
    }
}

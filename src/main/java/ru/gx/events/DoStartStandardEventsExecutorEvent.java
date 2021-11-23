package ru.gx.events;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Объект-событие.<br/>
 * Публикация данного события запускает StandardEventsExecutor.
 * Слушателем данного является сам StandardEventsExecutor.
 */
@SuppressWarnings("unused")
public class DoStartStandardEventsExecutorEvent extends ApplicationEvent implements Event {
    protected DoStartStandardEventsExecutorEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new DoStartStandardEventsExecutorEvent(source);
        publisher.publishEvent(event);
    }

    public static void publish(@NotNull final ApplicationContext context, @NotNull final Object source) {
        final var event = new DoStartStandardEventsExecutorEvent(source);
        context.publishEvent(event);
    }
}

package ru.gx.simpleworker;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import ru.gx.events.Event;

/**
 * Объект-событие.<br/>
 * Публикация данного события запускает Worker.
 * Слушателем данного является сам Worker.
 */
@SuppressWarnings("unused")
public class DoStartSimpleWorkerEvent extends ApplicationEvent implements Event {
    protected DoStartSimpleWorkerEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new DoStartSimpleWorkerEvent(source);
        publisher.publishEvent(event);
    }

    public static void publish(@NotNull final ApplicationContext context, @NotNull final Object source) {
        final var event = new DoStartSimpleWorkerEvent(source);
        context.publishEvent(event);
    }
}

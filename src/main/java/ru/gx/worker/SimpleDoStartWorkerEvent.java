package ru.gx.worker;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Объект-событие.<br/>
 * Публикация данного события запускает Worker.
 * Слушателем данного является сам Worker.
 */
@SuppressWarnings("unused")
public class SimpleDoStartWorkerEvent extends AbstractDoStartWorkerEvent {
    protected SimpleDoStartWorkerEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new SimpleDoStartWorkerEvent(source);
        publisher.publishEvent(event);
    }

    public static void publish(@NotNull final ApplicationContext context, @NotNull final Object source) {
        final var event = new SimpleDoStartWorkerEvent(source);
        context.publishEvent(event);
    }
}

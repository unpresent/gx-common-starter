package ru.gx.worker;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Объект-событие.<br/>
 * Публикация данного события запускает Worker.
 * Слушателем данного является сам Worker.
 */
@SuppressWarnings("unused")
public class SimpleDoStopWorkerEvent extends AbstractDoStopWorkerEvent {
    protected SimpleDoStopWorkerEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new SimpleDoStopWorkerEvent(source);
        publisher.publishEvent(event);
    }
}

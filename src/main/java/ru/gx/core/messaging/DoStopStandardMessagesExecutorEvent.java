package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Объект-событие.<br/>
 * Публикация данного события останавливает StandardEventsExecutor.
 * Слушателем данного является сам StandardEventsExecutor.
 */
@SuppressWarnings("unused")
public class DoStopStandardMessagesExecutorEvent extends ApplicationEvent {
    protected DoStopStandardMessagesExecutorEvent(@NotNull final Object source) {
        super(source);
    }

    public static void publish(@NotNull final ApplicationEventPublisher publisher, @NotNull final Object source) {
        final var event = new DoStopStandardMessagesExecutorEvent(source);
        publisher.publishEvent(event);
    }

    public static void publish(@NotNull final ApplicationContext context, @NotNull final Object source) {
        final var event = new DoStopStandardMessagesExecutorEvent(source);
        context.publishEvent(event);
    }
}

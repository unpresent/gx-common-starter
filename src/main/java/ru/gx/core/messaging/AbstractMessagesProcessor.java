package ru.gx.core.messaging;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Исполнитель событий.
 */
@Slf4j
public abstract class AbstractMessagesProcessor implements MessagesProcessor {

    @NotNull
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Накопитель статистики обработки.
     */
    @NotNull
    private final StandardMessagesExecutorStatisticsInfo messagesStatisticsInfo;

    protected AbstractMessagesProcessor(@NotNull final ApplicationEventPublisher eventPublisher, @NotNull final StandardMessagesExecutorStatisticsInfo messagesStatisticsInfo) {
        super();
        this.eventPublisher = eventPublisher;
        this.messagesStatisticsInfo = messagesStatisticsInfo;
    }

    /**
     * Извлечь событие из контейнера очередей и обработать его (вызвать обработчик).
     * @param queue Контейнер очередей.
     * @return True - событие было извлечено и обработано. False - нет событий в очереди.
     */
    @Override
    public Message<? extends MessageHeader, ? extends MessageBody> pollMessage(@NotNull MessagesPrioritizedQueue queue) {
        final var event = queue.pollMessage();
        if (event == null) {
            log.debug("No messages in queue {}", queue.getName());
        } else {
            log.debug("Polled message " + event.getClass().getName());
        }
        return event;
    }

    /**
     * Обработка одного сообщения.
     * @param message Сообщение, которое бросаем на обработку через this.eventPublisher.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractMessagesProcessor processMessage(@NotNull Message<? extends MessageHeader, ? extends MessageBody> message) {
        this.messagesStatisticsInfo.messageExecuteStarting();
        try {
            this.eventPublisher.publishEvent(message);
        } finally {
            this.messagesStatisticsInfo.messagesExecuteFinished(message);
        }
        return this;
    }
}

package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс исполнителя событий.
 */
@SuppressWarnings("UnusedReturnValue")
public interface MessagesProcessor {

    /**
     * Извлечь событие из контейнера очередей.
     * @param queue Контейнер очередей.
     * @return Событие, которое было извлечено и обработано. Null - нет событий в очереди.
     */
    @Nullable
    Message<? extends MessageHeader, ? extends MessageBody> pollMessage(@NotNull final MessagesPrioritizedQueue queue);

    /**
     * Вызвать обработчик события.
     * @param message Событие.
     * @return this.
     */
    @NotNull
    MessagesProcessor processMessage(@NotNull final Message<? extends MessageHeader, ? extends MessageBody> message);
}

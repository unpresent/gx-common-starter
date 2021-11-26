package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс исполнителя событий.
 */
@SuppressWarnings("UnusedReturnValue")
public interface EventsProcessor {

    /**
     * Извлечь событие из контейнера очередей.
     * @param queue Контейнер очередей.
     * @return Событие, которое было извлечено и обработано. Null - нет событий в очереди.
     */
    @Nullable
    Event pollEvent(@NotNull final EventsPrioritizedQueue queue);

    /**
     * Вызвать обработчик события.
     * @param event Событие.
     * @return this.
     */
    @NotNull
    EventsProcessor processEvent(@NotNull final Event event);
}

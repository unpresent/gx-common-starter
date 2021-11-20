package ru.gx.events;

import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс исполнителя событий.
 */
public interface EventsProcessor {

    /**
     * Извлечь событие из контейнера очередей и обработать его (вызвать обработчик).
     * @param queue Контейнер очередей.
     * @return Cобытие, которое было извлечено и обработано. Null - нет событий в очереди.
     */
    Event pollAndProcessEvent(@NotNull final EventsPrioritizedQueue queue);
}

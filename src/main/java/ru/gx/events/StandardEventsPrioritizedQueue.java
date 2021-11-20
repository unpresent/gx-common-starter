package ru.gx.events;

import org.jetbrains.annotations.NotNull;

/**
 * Контейнер приоритезированных очередей.
 */
public class StandardEventsPrioritizedQueue extends AbstractEventsPrioritizedQueue {
    public static final String DEFAULT_NAME = "standard-events-queue";
    /**
     * Конструктор контейнера очередей.
     * @param name Имя контейнера. Используется для логирования.
     */
    public StandardEventsPrioritizedQueue(@NotNull final String name) {
        super(name);
    }
}

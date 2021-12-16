package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Контейнер приоритезированных очередей.
 */
public class StandardMessagesPrioritizedQueue extends AbstractMessagesPrioritizedQueue {
    public static final String DEFAULT_NAME = "standard-messages-queue";
    /**
     * Конструктор контейнера очередей.
     * @param name Имя контейнера. Используется для логирования.
     */
    public StandardMessagesPrioritizedQueue(@NotNull final String name) {
        super(name);
    }
}

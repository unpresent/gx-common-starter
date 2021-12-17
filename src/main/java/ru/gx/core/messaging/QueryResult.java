package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс ответа на запрос.
 */
public interface QueryResult<B extends MessageBody>
        extends Message<QueryResultHeader, B> {
    @SuppressWarnings("unused")
    @NotNull
    String getQueryId();
}

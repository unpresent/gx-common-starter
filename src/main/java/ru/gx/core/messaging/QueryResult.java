package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс ответа на запрос.
 */
public interface QueryResult<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
    @SuppressWarnings("unused")
    @NotNull
    String getQueryId();
}

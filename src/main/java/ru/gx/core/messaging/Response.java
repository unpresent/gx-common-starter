package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс ответа на заявку.
 */
public interface Response< B extends MessageBody>
        extends Message<ResponseHeader, B> {
    /**
     * @return  Идентификатор заявки. @see {@link Request}
     */
    @SuppressWarnings("unused")
    @NotNull
    String getRequestId();
}

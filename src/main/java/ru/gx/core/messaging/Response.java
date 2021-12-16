package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс ответа на заявку.
 */
public interface Response<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
    /**
     * @return  Идентификатор заявки. @see {@link Request}
     */
    @SuppressWarnings("unused")
    @NotNull
    String getRequestId();
}

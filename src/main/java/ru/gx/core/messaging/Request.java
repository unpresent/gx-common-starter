package ru.gx.core.messaging;

/**
 * Интерфейс заявки.
 */
public interface Request<B extends MessageBody>
        extends Message<RequestHeader, B> {
}

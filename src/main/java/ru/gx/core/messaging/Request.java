package ru.gx.core.messaging;

/**
 * Интерфейс заявки.
 */
public interface Request<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
}

package ru.gx.core.messaging;

/**
 * Интерфейс запроса.
 */
public interface Query<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
}

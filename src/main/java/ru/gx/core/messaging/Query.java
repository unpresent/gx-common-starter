package ru.gx.core.messaging;

/**
 * Интерфейс запроса.
 */
public interface Query<B extends MessageBody>
        extends Message<QueryHeader, B> {
}

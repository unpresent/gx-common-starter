package ru.gx.core.messaging;

/**
 * Интерфейс сообщения публикации данных (DataObject или DataPackage)
 * @param <B> Тип тела сообщения.
 */
public interface DataPublish<B extends MessageBody>
        extends Message<DataPublishHeader, B> {
}

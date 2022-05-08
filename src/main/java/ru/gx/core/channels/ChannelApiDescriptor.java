package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;
import ru.gx.core.messaging.MessageKind;

/**
 * Интерфейс API канала передачи данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ChannelApiDescriptor<M extends Message<? extends MessageBody>> {

    /**
     * Имя канала передачи данных.
     */
    @NotNull
    String getName();

    /**
     * Режим сериализации: Json-строки или Байты.
     */
    @NotNull
    SerializeMode getSerializeMode();

    /**
     * @return Вид сообщений, которые могут передаваться в канале.
     */
    @NotNull
    MessageKind getMessageKind();

    /**
     * @return Тип сообщений, которые могут передаваться в канале.
     */
    @NotNull
    String getMessageType();

    /**
     * @return Версия типа сообщений, которые могут передаваться в данном канале.
     */
    int getVersion();

    /**
     * @return Класс сообщений, которые будут передаваться в канале.
     */
    @NotNull
    Class<M> getMessageClass();
}

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
public interface ChannelApiDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>> {

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
     * @param serializeMode Режим сериализации: Json-строки или Байты.
     * @return this.
     */
    @NotNull
    ChannelApiDescriptor<M> setSerializeMode(@NotNull final SerializeMode serializeMode);

    /**
     * @return Вид сообщения, которые могут передаваться в канале.
     */
    @NotNull
    MessageKind getMessageKind();

    /**
     * @return Тип сообщения, которые могут передаваться в канале.
     */
    @NotNull
    String getMessageType();

    /**
     * @return Класс сообщений, которые будут передаваться в канале.
     */
    @NotNull
    Class<M> getMessageClass();
}

package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

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
     * @return Класс сообщений, которые будут передаваться в канале.
     */
    @Nullable
    Class<M> getMessageClass();
}

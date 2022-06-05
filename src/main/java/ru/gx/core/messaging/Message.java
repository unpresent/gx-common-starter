package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelHandlerDescriptor;

/**
 * Базовый интерфейс для разных видов сообщений.
 */
@SuppressWarnings("unused")
public interface Message<B extends MessageBody> extends MetadataGetter, MetadataSetter {

    /**
     * @return Заголовок сообщения.
     */
    @NotNull
    MessageHeader getHeader();

    /**
     * @return Тело сообщения.
     */
    @Nullable
    B getBody();

    /**
     * @return Неиспользуемые данные, которые надо проигнорировать.
     */
    @Nullable
    MessageCorrelation getCorrelation();

    /**
     * @return Описатель канала, по которому получены данные.
     */
    @JsonIgnore
    @NotNull
    ChannelHandlerDescriptor<? extends Message<B>> getChannelDescriptor();

    /**
     * Установка описателя канала
     * @param channelDescriptor Описатель канала, по которому получены данные.
     */
    @JsonIgnore
    @NotNull
    Message<B> setChannelDescriptor(@NotNull final ChannelHandlerDescriptor<? extends Message<B>> channelDescriptor);

    /**
     * @return Подготовлено ли для обработки
     */
    boolean handleReady();
}

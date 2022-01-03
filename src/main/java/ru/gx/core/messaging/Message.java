package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelHandlerDescriptor;

/**
 * Базовый интерфейс для разных видов сообщений.
 */
@SuppressWarnings("unused")
public interface Message<H extends MessageHeader, B extends MessageBody> extends MetadataGetter, MetadataSetter {

    /**
     * @return Заголовок сообщения.
     */
    @NotNull
    H getHeader();

    /**
     * @return Тело сообщения.
     */
    @Nullable
    B getBody();

    /**
     * @return Не используемые данные, которые надо проигнорировать.
     */
    @Nullable
    MessageCorrelation getCorrelation();

    /**
     * @return Описатель канала, по которому получены данные.
     */
    @JsonIgnore
    @NotNull
    ChannelHandlerDescriptor<? extends Message<H, B>> getChannelDescriptor();

    /**
     * Установка описателя канала
     * @param channelDescriptor Описатель канала, по которому получены данные.
     */
    @JsonIgnore
    @NotNull
    Message<H, B> setChannelDescriptor(@NotNull final ChannelHandlerDescriptor<? extends Message<H, B>> channelDescriptor);
}

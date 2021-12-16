package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public interface MessageHeader {

    /**
     * @return Идентификатор сообщения.
     */
    @NotNull
    String getId();

    /**
     * @return Вид сообщения. @see {@link MessageKind}
     */
    @NotNull
    MessageKind getKind();

    /**
     * @return Код типа сообщения.
     */
    @NotNull
    String getType();

    /**
     * @return Система-источник формирования сообщения.
     */
    @Nullable
    String getSourceSystem();

    /**
     * @return Дата создания сообщения.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Nullable
    LocalDateTime getCreatedDateTime();

    /**
     * @return Версия формата сообщения.
     */
    int getVersion();
}

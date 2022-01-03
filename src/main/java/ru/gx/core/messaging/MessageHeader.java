package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;

@SuppressWarnings("unused")
public interface MessageHeader {

    /**
     * @return Идентификатор сообщения.
     */
    @NotNull
    String getId();

    /**
     * @return Идентификатор вышестоящего сообщения (при обработке которого родилось данное сообщение).
     */
    @Nullable
    String getParentId();

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
    ZonedDateTime getCreatedDateTime();

    /**
     * @return Версия формата сообщения.
     */
    int getVersion();
}

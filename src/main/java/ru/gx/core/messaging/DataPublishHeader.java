package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class DataPublishHeader extends AbstractMessageHeader {
    /**
     * Вид сообщения. @see MessageKind.
     */
    @NotNull
    public MessageKind getKind() {
        return MessageKind.DataPublish;
    }

    /**
     * Конструктор заголовка сообщения публикации данных.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    @JsonCreator
    public DataPublishHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("parentId") @Nullable final String parentId,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTimeUtc,
            @JsonProperty("version") final int version
    ) {
        super(id, parentId, kind, type, version, sourceSystem, createdDateTimeUtc);
    }

    /**
     * Конструктор заголовка сообщения публикации данных.
     * @param id Идентификатор сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    public DataPublishHeader(
            @NotNull final String id,
            @Nullable final String parentId,
            @NotNull final String type,
            final int version,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        super(id, parentId, type, version, sourceSystem, createdDateTimeUtc);
    }
}

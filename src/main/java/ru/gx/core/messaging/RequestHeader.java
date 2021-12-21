package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class RequestHeader extends AbstractMessageHeader {
    /**
     * Вид сообщения. @see MessageKind.
     */
    @NotNull
    public MessageKind getKind() {
        return MessageKind.Request;
    }

    /**
     * Конструктор заголовка заявки.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    @SuppressWarnings("unused")
    @JsonCreator
    public RequestHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("parentId") @Nullable final String parentId,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("version") final int version,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        super(id, parentId, kind, type, version, sourceSystem, createdDateTimeUtc);
    }

    /**
     * Конструктор заголовка заявки.
     * @param id Идентификатор сообщения.
     * @param parentId Идентификатор родительского сообщения (в рамках обработки которого порождается данное).
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    public RequestHeader(
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

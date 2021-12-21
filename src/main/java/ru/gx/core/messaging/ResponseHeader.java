package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public class ResponseHeader extends AbstractMessageHeader {
    /**
     * Вид сообщения. @see MessageKind.
     */
    @NotNull
    public MessageKind getKind() {
        return MessageKind.Response;
    }

    /**
     * Конструктор заголовка ответа на заявку.
     * @param id Идентификатор сообщения.
     * @param parentId Идентификатор исходной заявки, для которой сформирован данный ответ.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    @JsonCreator
    public ResponseHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("parentId") @NotNull final String parentId,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("version") final int version,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        super(id, parentId, kind, type, version, sourceSystem, createdDateTimeUtc);
    }

    /**
     * Конструктор заголовка ответа на заявку.
     * @param id Идентификатор сообщения.
     * @param parentId Идентификатор исходной заявки, для которой сформирован данный ответ.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    public ResponseHeader(
            @NotNull final String id,
            @NotNull final String parentId,
            @NotNull final String type,
            final int version,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        super(id, parentId, type, version, sourceSystem, createdDateTimeUtc);
    }
}

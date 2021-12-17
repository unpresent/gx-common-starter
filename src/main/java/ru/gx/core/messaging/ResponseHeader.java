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

    @Getter
    @NotNull
    public final String requestId;

    /**
     * Конструктор заголовка ответа на заявку.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTime Дата и время создания сообщения.
     * @param version Версия сообщения.
     * @param requestId Идентификатор исходной заявки, для которой сформирован данный ответ.
     */
    @JsonCreator
    public ResponseHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final LocalDateTime createdDateTime,
            @JsonProperty("version") final int version,
            @JsonProperty("requestId") @NotNull final String requestId
    ) {
        super(id, kind, type, sourceSystem, createdDateTime, version);
        this.requestId = requestId;
    }

    /**
     * Конструктор заголовка ответа на заявку.
     * @param id Идентификатор сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTime Дата и время создания сообщения.
     * @param version Версия сообщения.
     * @param requestId Идентификатор исходной заявки, для которой сформирован данный ответ.
     */
    public ResponseHeader(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version,
            @NotNull final String requestId
    ) {
        super(id, type, sourceSystem, createdDateTime, version);
        this.requestId = requestId;
    }
}

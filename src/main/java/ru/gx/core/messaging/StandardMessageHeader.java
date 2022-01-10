package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.utils.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "id")
@ToString
public class StandardMessageHeader implements MessageHeader {
    /**
     * Идентификатор сообщения.
     */
    @Getter
    @NotNull
    private final String id;

    /**
     * Идентификатор вышестоящего сообщения (при обработке которого родилось данное сообщение).
     */
    @Getter
    @Nullable
    private final String parentId;

    /**
     * Вид сообщения. @see MessageKind.
     */
    @Getter
    @NotNull
    public final MessageKind kind;

    /**
     * Код типа сообщения.
     */
    @Getter
    @NotNull
    public String type;

    /**
     * Источник формирования сообщения.
     */
    @Getter
    @Nullable
    private final String sourceSystem;

    /**
     * Дата создания сообщения.
     */
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss.SSS Z", timezone = "UTC")
    // @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Getter
    @NotNull
    private final ZonedDateTime createdDateTime;

    @Getter
    private final int version;

    /**
     * Конструктор заголовка сообщения.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTime Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    @JsonCreator
    public StandardMessageHeader(
            @JsonProperty("id") @NotNull final String id,
            @JsonProperty("parentId") @Nullable final String parentId,
            @JsonProperty("kind") @NotNull final MessageKind kind,
            @JsonProperty("type") @NotNull final String type,
            @JsonProperty("version") final int version,
            @JsonProperty("systemSource") @Nullable final String sourceSystem,
            @JsonProperty("createdDateTime") @NotNull final ZonedDateTime createdDateTime
    ) {
        this.id = id;
        this.kind = kind;
        this.parentId = parentId;
        this.type = type;
        this.version = version;
        this.sourceSystem = sourceSystem;
        this.createdDateTime = createdDateTime;
    }
}

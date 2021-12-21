package ru.gx.core.messaging;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "id")
@ToString
public abstract class AbstractMessageHeader implements MessageHeader {
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
    @NotNull
    public abstract MessageKind getKind();

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
    @Getter
    @NotNull
    private final LocalDateTime createdDateTimeUtc;

    @Getter
    private final int version;

    /**
     * Конструктор заголовка сообщения.
     * @param id Идентификатор сообщения.
     * @param kind Вид сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    protected AbstractMessageHeader(
            @NotNull final String id,
            @Nullable final String parentId,
            @NotNull final MessageKind kind,
            @NotNull final String type,
            final int version,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        this(id, parentId, type, version, sourceSystem, createdDateTimeUtc);
        checkMessageKind(kind, getKind());
    }

    /**
     * Конструктор заголовка сообщения.
     * @param id Идентификатор сообщения.
     * @param type Тип сообщения.
     * @param sourceSystem Система-источник.
     * @param createdDateTimeUtc Дата и время создания сообщения.
     * @param version Версия сообщения.
     */
    protected AbstractMessageHeader(
            @NotNull final String id,
            @Nullable final String parentId,
            @NotNull final String type,
            final int version,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTimeUtc
    ) {
        this.id = id;
        this.parentId = parentId;
        this.type = type;
        this.version = version;
        this.sourceSystem = sourceSystem;
        this.createdDateTimeUtc = createdDateTimeUtc;
    }

    /**
     * Проверка на соответствие вида сообщения, которое передано извне (при десериализации), виду сообщения, который определяет наследник.
     * @param kind Вид сообщения, которое передали извне.
     * @param targetKind Вид сообщения, который определяет наследник.
     */
    protected void checkMessageKind(@NotNull final MessageKind kind, @NotNull final MessageKind targetKind) {
        if (kind != targetKind) {
            throw new InvalidParameterException("Incorrect message kind " + kind + "! It should be " + targetKind);
        }
    }
}

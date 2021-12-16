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
    private final LocalDateTime createdDateTime;

    @Getter
    private final int version;

    protected AbstractMessageHeader(
            @NotNull final String id,
            @NotNull final MessageKind kind,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version
    ) {
        this(id, type, sourceSystem, createdDateTime, version);
        checkMessageKind(kind, getKind());
    }

    protected AbstractMessageHeader(
            @NotNull final String id,
            @NotNull final String type,
            @Nullable final String sourceSystem,
            @NotNull final LocalDateTime createdDateTime,
            final int version
    ) {
        this.id = id;
        this.type = type;
        this.sourceSystem = sourceSystem;
        this.createdDateTime = createdDateTime;
        this.version = version;
    }

    protected void checkMessageKind(@NotNull final MessageKind kind, @NotNull final MessageKind targetKind) {
        if (kind != targetKind) {
            throw new InvalidParameterException("Incorrect message kind " + kind + "! It should be " + targetKind);
        }
    }
}

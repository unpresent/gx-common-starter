package ru.gx.core.channels;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageKind;
import ru.gx.core.messaging.MessageTypesRegistrator;

/**
 * Описатель API канала передачи данных.
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "name")
@ToString
@SuppressWarnings("unused")
public abstract class AbstractChannelApiDescriptor<M extends Message<? extends MessageBody>>
        implements ChannelApiDescriptor<M> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Имя канала передачи данных.
     */
    @Getter
    @NotNull
    private final String name;

    /**
     * Режим сериализации: Json-строки или Байты.
     */
    @Getter
    @NotNull
    private final SerializeMode serializeMode;

    /**
     * Вид сообщений, которые передаются в данном канале.
     */
    @Getter
    @NotNull
    private final MessageKind messageKind;

    /**
     * Тип сообщений, которые передаются в данном канале.
     */
    @Getter
    @NotNull
    private final String messageType;

    @Getter
    private final int version;

    /**
     * Класс сообщений, в экземпляры передаются в данном канале.
     */
    @Getter
    @NotNull
    private final Class<M> messageClass;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractChannelApiDescriptor(
            @NotNull final String name,
            @NotNull final SerializeMode serializeMode,
            @NotNull final Class<M> messageClass,
            @NotNull final MessageKind messageKind,
            @NotNull final String messageType,
            final int version
    ) {
        this.name = name;
        this.serializeMode = serializeMode;
        this.messageKind = messageKind;
        this.messageType = messageType;
        this.version = version;
        this.messageClass = messageClass;
        MessageTypesRegistrator.checkType(this.messageKind, this.messageType, this.version, this.messageClass);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

package ru.gx.core.channels;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

/**
 * Описатель API канала передачи данных.
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "name")
@ToString
@SuppressWarnings("unused")
public abstract class AbstractChannelApiDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>>
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
     * Признак того, что данный канал включен.
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
            @NotNull final Class<M> messageClass
    ) {
        this.name = name;
        this.serializeMode = serializeMode;
        this.messageClass = messageClass;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

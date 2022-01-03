package ru.gx.core.channels;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

import java.lang.reflect.Constructor;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractChannelHandlerDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>>
        implements ChannelHandlerDescriptor<M> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Конфигурация, которой принадлежит описатель.
     */
    @Getter(PROTECTED)
    @NotNull
    private final ChannelsConfiguration owner;

    @Getter
    @NotNull
    private final ChannelApiDescriptor<M> api;

    @Getter
    @NotNull
    private final ChannelDirection direction;

    @Getter
    private int priority;

    /**
     * Признак того, что данный канал включен.
     */
    @Getter
    private boolean enabled = true;

    /**
     * Признак того, что описатель инициализирован.
     */
    @Getter
    private boolean initialized = false;

    // TODO: WTF?
    @NotNull
    private final Constructor<M> messageConstructor;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">

    @SuppressWarnings("unchecked")
    protected AbstractChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final ChannelApiDescriptor<M> api,
            @NotNull final ChannelDirection direction,
            @Nullable final AbstractChannelDescriptorsDefaults defaults
    ) {
        this.owner = owner;
        this.api = api;
        this.direction = direction;

        final var messageClass = this.getApi().getMessageClass();
        final var constructor = Arrays.stream(messageClass.getConstructors())
                .filter(c -> {
                    final var paramsTypes = c.getParameterTypes();
                    return paramsTypes.length == 3
                            && MessageHeader.class.isAssignableFrom(paramsTypes[0])
                            && MessageBody.class.isAssignableFrom(paramsTypes[1])
                            && Map.class.isAssignableFrom(paramsTypes[2]);
                })
                .findFirst();
        if (constructor.isEmpty()) {
            throw new ChannelConfigurationException("Can't find constructor(MessageHeader, MessageBody, Map) for class: " + messageClass.getName());
        }
        this.messageConstructor = (Constructor<M>) constructor.get();
    }

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor<M> init() throws InvalidParameterException {
        this.initialized = true;
        this.owner.internalRegisterDescriptor(this);
        return this;
    }

    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor<M> unInit() {
        this.initialized = false;
        this.owner.internalUnregisterDescriptor(this);
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">
    protected void checkMutable(@NotNull final String propertyName) {
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor of channel " + this.getApi().getName() + " can't change property " + propertyName + " after initialization!");
        }
    }

    /**
     * Установка приоритета у данного канала.
     *
     * @param priority приоритет.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor<M> setPriority(final int priority) {
        checkMutable("priority");
        this.priority = priority;
        return this;
    }

    /**
     * @param enabled режим включения/выключения канала.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor<M> setEnabled(final boolean enabled) {
        checkMutable("enabled");
        this.enabled = enabled;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

}

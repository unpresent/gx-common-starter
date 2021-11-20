package ru.gx.channels;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractChannelDescriptor implements ChannelDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Конфигурация, которой принадлежит описатель.
     */
    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private final ChannelsConfiguration owner;

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private final ChannelDirection direction;

    @Getter
    private int priority;

    /**
     * Режим данных в канале: Пообъектно и пакетно.
     */
    @Getter
    @NotNull
    private ChannelMessageMode messageMode;

    /**
     * Режим сериализации: Json-строки или Байты.
     */
    @Getter
    @NotNull
    private SerializeMode serializeMode;

    /**
     * Признак того, что описатель инициализирован.
     */
    @Getter
    private boolean initialized = false;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">

    protected AbstractChannelDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final String name,
            @NotNull final ChannelDirection direction,
            @Nullable final AbstractChannelDescriptorsDefaults defaults
    ) {
        this.owner = owner;
        this.name = name;
        this.direction = direction;
        this.messageMode = ChannelMessageMode.Object;
        this.serializeMode = SerializeMode.JsonString;
        if (defaults != null) {
            this
                    .setMessageMode(defaults.getMessageMode())
                    .setSerializeMode(defaults.getSerializeMode());
        }
    }

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelDescriptor init() throws InvalidParameterException {
        this.initialized = true;
        this.owner.internalRegisterDescriptor(this);
        return this;
    }

    @Override
    @NotNull
    public AbstractChannelDescriptor unInit() {
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">
    protected void checkChangeable(@NotNull final String propertyName) {
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor of channel " + getName() + " can't change property " + propertyName + " after initialization!");
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
    public AbstractChannelDescriptor setPriority(final int priority) {
        checkChangeable("priority");
        this.priority = priority;
        return this;
    }

    /**
     * Установка режима данных в канале.
     *
     * @param messageMode режим данных в очереди.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelDescriptor setMessageMode(@NotNull final ChannelMessageMode messageMode) {
        checkChangeable("messageMode");
        this.messageMode = messageMode;
        return this;
    }

    /**
     * @param serializeMode Режим сериализации: Json-строки или Байты.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelDescriptor setSerializeMode(@NotNull final SerializeMode serializeMode) {
        checkChangeable("serializeMode");
        this.serializeMode = serializeMode;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

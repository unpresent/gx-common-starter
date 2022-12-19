package ru.gx.core.channels;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

import java.security.InvalidParameterException;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractChannelHandlerDescriptor
        implements ChannelHandlerDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Конфигурация, которой принадлежит описатель.
     */
    @Getter
    @NotNull
    private final ChannelsConfiguration owner;

    /**
     * Имя канала.
     */
    @Nullable
    private final String channelName;

    /**
     * Описатель API канала.
     */
    @Getter
    @Nullable
    private final ChannelApiDescriptor<? extends Message<? extends MessageBody>> api;

    /**
     * Направление канала.
     */
    @Getter
    @NotNull
    private final ChannelDirection direction;

    /**
     * Приоритет.
     */
    @Getter
    private int priority;

    @Getter
    @NotNull
    private OnErrorBehavior onErrorBehavior = OnErrorBehavior.StopProcessOnError;

    /**
     * Признак того, что данный канал включен.
     */
    @Getter
    private boolean enabled = true;

    /**
     * Блокирующая работу канала ошибка
     */
    @Getter
    @Nullable
    private volatile Exception blockingError = null;

    @Getter
    @NotNull
    private final ChannelExecuteStatistics executeStatistics;

    /**
     * Признак того, что описатель инициализирован.
     */
    @Getter
    private boolean initialized = false;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final ChannelApiDescriptor<? extends Message<? extends MessageBody>> api,
            @NotNull final ChannelDirection direction,
            @Nullable final AbstractChannelDescriptorsDefaults defaults
    ) {
        this.owner = owner;
        this.api = api;
        this.channelName = null;
        this.direction = direction;
        this.executeStatistics = new ChannelExecuteStatistics(this, this.owner.getMeterRegistry());
        if (defaults != null) {
            this.onErrorBehavior = defaults.getOnErrorBehavior();
        }
    }

    protected AbstractChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final String channelName,
            @NotNull final ChannelDirection direction,
            @Nullable final AbstractChannelDescriptorsDefaults defaults
    ) {
        this.owner = owner;
        this.api = null;
        this.channelName = channelName;
        this.direction = direction;
        this.executeStatistics = new ChannelExecuteStatistics(this, this.owner.getMeterRegistry());
        if (defaults != null) {
            this.onErrorBehavior = defaults.getOnErrorBehavior();
        }
    }

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor init() throws InvalidParameterException {
        this.initialized = true;
        this.owner.internalRegisterDescriptor(this);
        return this;
    }

    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor unInit() {
        this.initialized = false;
        this.owner.internalUnregisterDescriptor(this);
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">

    /**
     * @return Имя канала. Если указан api, то берется из него.
     */
    @NotNull
    @Override
    public String getChannelName() {
        if (getApi() != null) {
            return getApi().getName();
        } else if (this.channelName == null) {
            throw new NullPointerException("Channel name is null");
        } else {
            return this.channelName;
        }
    }

    @Override
    @NotNull
    public ChannelState getState() {
        return this.blockingError != null ?
                ChannelState.BlockedByError :
                (this.enabled ? ChannelState.Enabled : ChannelState.Disabled);
    }

    /**
     * @return Заблокирован ли данный канал ошибкой
     */
    @Override
    public boolean isBlockedByError() {
        return this.blockingError != null;
    }

    protected void checkMutable(@NotNull final String propertyName) {
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor of channel " + getChannelName() + " can't change property " + propertyName + " after initialization!");
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
    public AbstractChannelHandlerDescriptor setPriority(final int priority) {
        checkMutable("priority");
        this.priority = priority;
        return this;
    }

    /**
     * Установка способа реагирования на ошибку при обработке сообщений в данном канале
     *
     * @param onErrorBehavior способа реагирования.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor setOnErrorBehavior(@NotNull final OnErrorBehavior onErrorBehavior) {
        checkMutable("onErrorBehavior");
        this.onErrorBehavior = onErrorBehavior;
        return this;
    }

    /**
     * @param enabled режим включения/выключения канала.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor setEnabled(final boolean enabled) {
        checkMutable("enabled");
        this.enabled = enabled;
        return this;
    }

    /**
     * Установка блокирующей ошибки в канале.
     *
     * @param error Сообщение об ошибке
     * @return this.
     */
    @Override
    @NotNull
    public AbstractChannelHandlerDescriptor setBlockingError(
            @NotNull final Exception error
    ) {
        this.blockingError = error;
        return this;
    }

    /**
     * Сброс состояния-ошибки в канале (ошибка ушла).
     *
     * @return this.
     */
    @Override
    @NotNull
    public ChannelHandlerDescriptor clearBlockingError() {
        this.blockingError = null;
        return this;
    }

    /**
     * Фиксируется факт обработки сообщения.
     *
     * @param timeMs Время, затраченное на обработку сообщения.
     */
    @Override
    public void recordMessageExecuted(@NotNull String workerName, long timeMs) {
        getExecuteStatistics().recordMessageExecuted(workerName, timeMs);
    }

    /**
     * Фиксируется факт обработки сообщения.
     *
     * @param timeMs Время, затраченное на обработку сообщения.
     */
    @Override
    public void recordMessagesExecuted(@NotNull String workerName, final long timeMs, final int count) {
        getExecuteStatistics().recordMessagesExecuted(workerName, timeMs, count);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

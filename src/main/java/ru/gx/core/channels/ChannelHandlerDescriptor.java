package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

import java.security.InvalidParameterException;

/**
 * Интерфейс обработчика канала передачи данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ChannelHandlerDescriptor {

    /**
     * @return Конфигурация-владелец данного описателя
     */
    @NotNull
    ChannelsConfiguration getOwner();

    /**
     * @return Имя канала. Если указан api, то берется из него.
     */
    @NotNull
    String getChannelName();

    /**
     * @return Описатель API-канала передачи данных.
     */
    @Nullable
    ChannelApiDescriptor<? extends Message<? extends MessageBody>> getApi();

    /**
     * @return Направление передачи данных.
     */
    @NotNull
    ChannelDirection getDirection();

    /**
     * Приоритет, с которым надо обрабатывать сообщения в данном канале.
     * 0 - высший.
     * > 0 - менее приоритетный.
     */
    int getPriority();

    /**
     * Установка приоритета у данного канала.
     *
     * @param priority приоритет.
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor setPriority(int priority);

    /**
     * @return Способ реагирования на ошибку при обработке сообщений в данном канале
     */
    @NotNull
    OnErrorBehavior getOnErrorBehavior();

    /**
     * Установка способа реагирования на ошибку при обработке сообщений в данном канале
     *
     * @param onErrorBehavior способа реагирования.
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor setOnErrorBehavior(@NotNull final OnErrorBehavior onErrorBehavior);

    /**
     * @return Сводное состояние канала с учётом флага enabled и наличия блокирующего error.
     */
    @NotNull
    ChannelState getState();

    /**
     * @return Включен ли данный канал.
     */
    boolean isEnabled();

    /**
     * Включение/отключение данного канала.
     *
     * @param enabled true - включить канал, false - выключить канал.
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor setEnabled(final boolean enabled);

    /**
     * @return Заблокирован ли данный канал ошибкой
     */
    boolean isBlockedByError();

    /**
     * @return Ошибка, которая блокирует работу канала
     */
    @Nullable
    Exception getBlockingError();

    /**
     * Установка блокирующей ошибки в канале.
     *
     * @param error    Сообщение об ошибке
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor setBlockingError(@NotNull final Exception error);

    /**
     * Сброс состояния-ошибки в канале (ошибка ушла).
     *
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor clearBlockingError();

    /**
     * Признак того, что описатель инициализирован.
     */
    boolean isInitialized();

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor init() throws InvalidParameterException;

    /**
     * Деинициализация - перевод в режим редактирования описателя. Правка заканчивается вызовом init().
     *
     * @return this.
     */
    ChannelHandlerDescriptor unInit();
}

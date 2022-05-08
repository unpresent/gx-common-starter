package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

import java.security.InvalidParameterException;

/**
 * Интерфейс обработчика канала передачи данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ChannelHandlerDescriptor<M extends Message<? extends MessageBody>> {

    /**
     * @return Описатель API-канала передачи данных.
     */
    @NotNull
    ChannelApiDescriptor<M> getApi();

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
     * @param priority приоритет.
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor<M> setPriority(int priority);

    /**
     * @return Включен ли данный канал.
     */
    boolean isEnabled();

    /**
     * Включение/отключение данного канала.
     * @param enabled true - включить канал, false - выключить канал.
     * @return this.
     */
    @NotNull
    ChannelHandlerDescriptor<M> setEnabled(final boolean enabled);

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
    ChannelHandlerDescriptor<M> init() throws InvalidParameterException;

    /**
     * Деинициализация - перевод в режим редактирования описателя. Правка заканчивается вызовом init().
     * @return this.
     */
    ChannelHandlerDescriptor<M> unInit();
}

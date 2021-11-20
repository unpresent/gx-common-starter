package ru.gx.channels;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

/**
 * Интерфейс описателя канала передачи данных.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ChannelDescriptor {

    /**
     * Имя канала передачи данных.
     */
    @NotNull
    String getName();

    /**
     * @return Направление передачи данных.
     */
    @NotNull
    ChannelDirection getDirection();

    /**
     * Приоритет, с которым надо обрабатывать сообщения в данном канале..
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
    ChannelDescriptor setPriority(int priority);

    /**
     * Режим данных в канале: Пообъектно и пакетно.
     */
    @NotNull
    ChannelMessageMode getMessageMode();

    /**
     * Установка режима данных в канале.
     * @param messageMode режим данных в очереди.
     * @return this.
     */
    @NotNull
    ChannelDescriptor setMessageMode(@NotNull final ChannelMessageMode messageMode);

    /**
     * Режим сериализации: Json-строки или Байты.
     */
    @NotNull
    SerializeMode getSerializeMode();

    /**
     * @param serializeMode Режим сериализации: Json-строки или Байты.
     * @return this.
     */
    @NotNull
    ChannelDescriptor setSerializeMode(@NotNull final SerializeMode serializeMode);

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
    ChannelDescriptor init() throws InvalidParameterException;

    ChannelDescriptor unInit();
}

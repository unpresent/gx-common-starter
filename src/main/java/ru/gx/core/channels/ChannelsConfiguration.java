package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ChannelsConfiguration extends InternalDescriptorsRegistrator {
    @NotNull
    String getConfigurationName();

    /**
     * Проверка регистрации описателя канала в конфигурации.
     *
     * @param channelName Имя канала.
     * @return true - описатель канала зарегистрирован.
     */
    boolean contains(@NotNull final String channelName);

    /**
     * Получение описателя канала по имени канала.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала.
     * @throws ChannelConfigurationException В случае отсутствия описателя канала с заданным именем бросается ошибка.
     */
    @NotNull
    ChannelDescriptor get(@NotNull final String channelName) throws ChannelConfigurationException;

    /**
     * Получение описателя канала по имени.
     *
     * @param channelName Имя канала, для которого требуется получить описатель.
     * @return Описатель канала. Если не найден, то возвращается null.
     */
    @Nullable
    ChannelDescriptor tryGet(@NotNull final String channelName);

    /**
     * Регистрация описателя обработчика одной очереди.
     *
     * @param topic           Топик, для которого создается описатель.
     * @param descriptorClass Класс описателя.
     * @return this.
     */
    @NotNull
    <D extends ChannelDescriptor>
    D newDescriptor(@NotNull final String topic, @NotNull final Class<D> descriptorClass) throws ChannelConfigurationException;

    /**
     * @return Настройки по умолчанию для новых описателей загрузки из топиков.
     */
    @NotNull
    AbstractChannelDescriptorsDefaults getDescriptorsDefaults();

    /**
     * @return Количество приоритетов.
     */
    int prioritiesCount();

    /**
     * Получение списка описателей обработчиков очередей по приоритету.
     *
     * @param priority Приоритет.
     * @return Список описателей обработчиков.
     */
    @Nullable
    Iterable<ChannelDescriptor> getByPriority(int priority);

    /**
     * @return Список всех описателей обработчиков очередей.
     */
    @NotNull
    Iterable<ChannelDescriptor> getAll();
}

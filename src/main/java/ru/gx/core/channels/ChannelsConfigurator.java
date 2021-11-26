package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;

/**
 * Реализатор данного интерфейса будет вызван после настройки всех бинов.
 * Задача реализатора данного интерфейса заключается в конфигурировании каналов у указанной конфигурации.
 */
@SuppressWarnings("unused")
public interface ChannelsConfigurator {
    /**
     * Вызывается после настройки бинов.
     * @param configuration Передается бин, реализующий интерфейс ChannelsConfiguration. Данный бин в методе реализации требуется настроить.
     */
    void configureChannels(@NotNull final ChannelsConfiguration configuration);
}

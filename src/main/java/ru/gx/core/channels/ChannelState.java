package ru.gx.core.channels;

/**
 * Состояния канала обработки
 */
public enum ChannelState {
    /**
     * Канал работает
     */
    Enabled,

    /**
     * Выключен
     */
    Disabled,

    /**
     * В канале ошибки, обработка невозможна
     */
    BlockedByError
}

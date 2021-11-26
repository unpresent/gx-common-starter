package ru.gx.core.channels;

/**
 * Ошибки в конфигурации описателей каналов.
 */
public class ChannelConfigurationException extends RuntimeException {
    public ChannelConfigurationException(String message) {
        super(message);
    }
}

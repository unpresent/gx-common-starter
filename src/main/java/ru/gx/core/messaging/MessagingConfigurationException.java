package ru.gx.core.messaging;

/**
 * Ошибки в конфигурации описателей каналов.
 */
public class MessagingConfigurationException extends RuntimeException {
    public MessagingConfigurationException(String message) {
        super(message);
    }
}

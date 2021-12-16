package ru.gx.core.channels;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.Collection;

import static lombok.AccessLevel.PROTECTED;

/**
 * Задача данного Bean-а вызвать настройщиков конфигураций обработки входящих потоков.
 */
@Deprecated
@Slf4j
public class ChannelsConfiguratorCaller {

    /**
     * Бин конфигуратора. Его должен реализовать прикладной программист.
     * Как правило, это Config самого приложения.
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private ChannelsConfigurator channelsConfigurator;

    /**
     * Список конфигураций в приложении.
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private Collection<ChannelsConfiguration> configurations;

    /**
     * Обработчик события о том, что все бины построены. Приложение готово к запуску.
     * Вызываем конфигураторы настройки каналов.
     */
    @SuppressWarnings("unused")
    @EventListener(ApplicationReadyEvent.class)
    @ConditionalOnProperty(value = "service.channels.configurator-caller.enabled", havingValue = "true")
    public void onApplicationApplicationReady(ApplicationReadyEvent e) {
        if (this.channelsConfigurator == null) {
            throw new BeanInitializationException("Not initialized bean ChannelsConfigurator!");
        }
        if (this.configurations == null) {
            throw new BeanInitializationException("Not initialized bean Collection<ChannelsConfiguration>!");
        }
        this.configurations.forEach(c -> {
            log.info("Starting configure ChannelsConfiguration: {}", c.getClass().getSimpleName());
            this.channelsConfigurator.configureChannels(c);
            log.info("Finished configure ChannelsConfiguration: {}", c.getClass().getSimpleName());
        });
    }
}

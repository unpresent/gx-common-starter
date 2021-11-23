package ru.gx.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.gx.channels.ChannelsConfiguratorCaller;
import ru.gx.events.*;
import ru.gx.settings.StandardSettingsController;
import ru.gx.simpleworker.*;

import static lombok.AccessLevel.PROTECTED;

@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesService.class)
public class CommonAutoConfiguration {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    private final static String DOT_ENABLED = ".enabled";
    private final static String DOT_NAME = ".name";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private ApplicationEventPublisher eventPublisher;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private MeterRegistry meterRegistry;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="ChannelsConfiguratorCaller">
    @Bean
    @ConditionalOnMissingBean
    public ChannelsConfiguratorCaller channelsConfiguratorCaller() {
        return new ChannelsConfiguratorCaller();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Settings Controller">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardSettingsController.STANDARD_SETTINGS_CONTROLLER_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardSettingsController simpleSettingsController() {
        return new StandardSettingsController();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Simple Worker">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public SimpleWorker simpleWorker(
            @Value("${" + SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_NAME + "}") @Nullable final String name,
            @NotNull final SimpleWorkerSettingsContainer settingsContainer
    ) {
        return new SimpleWorker(StringUtils.hasLength(name) ? name : SimpleWorker.WORKER_DEFAULT_NAME, settingsContainer, this.meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public SimpleWorkerSettingsContainer simpleWorkerSettingsContainer() {
        return new SimpleWorkerSettingsContainer();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Events Executor">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardEventsExecutorSettingsContainer standardEventsExecutorSettingsContainer() {
        return new StandardEventsExecutorSettingsContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardEventsExecutor standardEventsExecutor(
            @Value("${" + StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_NAME + "}") String name,
            @NotNull final StandardEventsExecutorSettingsContainer settingsContainer
    ) {
        return new StandardEventsExecutor(
                StringUtils.hasLength(name) ? name : StandardEventsExecutor.WORKER_DEFAULT_NAME,
                settingsContainer,
                this.eventPublisher,
                this.meterRegistry
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public StandardEventsPrioritizedQueue standardEventsPrioritizedQueue(
            @Value("${" + StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_NAME + "}") final String name,
            @NotNull final StandardEventsExecutorSettingsContainer executorSettings
    ) {
        final var queue = new StandardEventsPrioritizedQueue(StringUtils.hasLength(name) ? name : StandardEventsPrioritizedQueue.DEFAULT_NAME);
        queue.init(executorSettings.maxQueueSize(), executorSettings.prioritiesCount());
        return queue;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
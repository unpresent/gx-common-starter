package ru.gx.core.config;

import io.micrometer.core.instrument.MeterRegistry;
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
import ru.gx.core.channels.*;
import ru.gx.core.messaging.DefaultMessagesFactory;
import ru.gx.core.messaging.StandardMessagesExecutor;
import ru.gx.core.messaging.StandardMessagesExecutorSettingsContainer;
import ru.gx.core.messaging.StandardMessagesPrioritizedQueue;
import ru.gx.core.settings.*;
import ru.gx.core.simpleworker.*;

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
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Settings Controller">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardSettingsController.STANDARD_SETTINGS_CONTROLLER_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardSettingsController standardSettingsController() {
        return new StandardSettingsController();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Simple Worker">
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public SimpleWorker simpleWorker(
            @Value("${" + SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_NAME + "}") @Nullable final String name,
            @NotNull final SimpleWorkerSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry
    ) {
        return new SimpleWorker(StringUtils.hasLength(name) ? name : SimpleWorker.WORKER_DEFAULT_NAME, settingsContainer, meterRegistry);
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
    @ConditionalOnProperty(value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardMessagesExecutorSettingsContainer standardEventsExecutorSettingsContainer() {
        return new StandardMessagesExecutorSettingsContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public StandardMessagesExecutor standardEventsExecutor(
            @Value("${" + StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_NAME + "}") String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MeterRegistry meterRegistry
    ) {
        return new StandardMessagesExecutor(
                StringUtils.hasLength(name) ? name : StandardMessagesExecutor.WORKER_DEFAULT_NAME,
                settingsContainer,
                eventPublisher,
                meterRegistry
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public StandardMessagesPrioritizedQueue standardEventsPrioritizedQueue(
            @Value("${" + StandardMessagesExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_NAME + "}") final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer executorSettings
    ) {
        final var queue = new StandardMessagesPrioritizedQueue(StringUtils.hasLength(name) ? name : StandardMessagesPrioritizedQueue.DEFAULT_NAME);
        queue.init(executorSettings.maxQueueSize(), executorSettings.prioritiesCount());
        return queue;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Standard Settings Controller">
    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public DefaultMessagesFactory defaultMessagesFactory(@Value("${service.name}") final String serviceName) {
        return new DefaultMessagesFactory(serviceName);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
package ru.gx.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.gx.events.*;
import ru.gx.settings.StandardSettingsController;
import ru.gx.worker.SimpleWorkerSettingsContainer;
import ru.gx.worker.SimpleOnIterationExecuteEvent;
import ru.gx.worker.SimpleOnStartingExecuteEvent;
import ru.gx.worker.SimpleOnStoppingExecuteEvent;
import ru.gx.worker.SimpleWorker;

import static lombok.AccessLevel.PROTECTED;

@Configuration
@EnableConfigurationProperties(ConfigurationPropertiesService.class)
public class CommonAutoConfiguration {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Contants">
    private final static String DOT_ENABLED = ".enabled";
    private final static String DOT_NAME = ".name";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
   @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private StandardEventsExecutorSettingsContainer executorSettings;
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
    public SimpleWorker simpleWorker(@Value("${" + SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_NAME + "}") @Nullable final String name) {
        return new SimpleWorker(StringUtils.hasLength(name) ? name : SimpleWorker.WORKER_DEFAULT_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public SimpleOnIterationExecuteEvent simpleOnIterationExecuteEvent(SimpleWorker source) {
        return new SimpleOnIterationExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public SimpleOnStartingExecuteEvent simpleOnStartingExecuteEvent(@NotNull final SimpleWorker source) {
        return new SimpleOnStartingExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = SimpleWorkerSettingsContainer.SIMPLE_WORKER_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public SimpleOnStoppingExecuteEvent simpleOnStoppingExecuteEvent(@NotNull final SimpleWorker source) {
        return new SimpleOnStoppingExecuteEvent(source);
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
    public StandardEventsExecutorStatisticsInfo standardEventsExecutorStatisticsInfo() {
        return new StandardEventsExecutorStatisticsInfo();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardEventsExecutor standardEventsExecutor(@Value("${" + StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + ".name}") String name) {
        return new StandardEventsExecutor(StringUtils.hasLength(name) ? name : StandardEventsExecutor.WORKER_DEFAULT_NAME);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    public StandardEventsPrioritizedQueue standardEventsPrioritizedQueue(
            @Value("${" + StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + DOT_NAME + "}") final String name
    ) {
        final var queue = new StandardEventsPrioritizedQueue(StringUtils.hasLength(name) ? name : StandardEventsPrioritizedQueue.DEFAULT_NAME);
        queue.init(executorSettings.maxQueueSize(), executorSettings.prioritiesCount());
        return queue;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public StandardEventsExecutorOnStartingExecuteEvent standardEventsExecutorOnStartingExecuteEvent(@NotNull final StandardEventsExecutor source) {
        return new StandardEventsExecutorOnStartingExecuteEvent(source);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StandardEventsExecutorSettingsContainer.STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + DOT_ENABLED, havingValue = "true")
    @Autowired
    public StandardEventsExecutorOnStoppingExecuteEvent standardEventsExecutorOnStoppingExecuteEvent(@NotNull final StandardEventsExecutor source) {
        return new StandardEventsExecutorOnStoppingExecuteEvent(source);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
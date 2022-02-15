package ru.gx.core.messaging;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.core.settings.UnknownApplicationSettingException;
import ru.gx.core.worker.AbstractWorkerSettingsContainer;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;
import static ru.gx.core.config.ConfigurationPropertiesService.StandardExecutor;
import static ru.gx.core.config.ConfigurationPropertiesService.StandardQueue;

@SuppressWarnings("unused")
public class StandardMessagesExecutorSettingsContainer extends AbstractWorkerSettingsContainer {
    public final static String STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX = "service.messages.standard-executor";
    public static final String STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX = "service.messages.standard-queue";

    private final static String SETTING_WAIT_ON_STOP_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_STOP_MS;
    private final static String SETTING_WAIT_ON_RESTART_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_RESTART_MS;
    private final static String SETTING_MIN_TIME_PER_ITERATION_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.MIN_TIME_PER_ITERATION_MS;
    private final static String SETTING_TIMOUT_RUNNER_LIFE_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.TIMEOUT_RUNNER_LIFE_MS;
    private final static String SETTING_PRINT_STATISTICS_EVERY_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.PRINT_STATISTICS_EVERY_MS;

    private final static String SETTING_PRINT_QUEUE_STATISTICS_EVERY_MS = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".print-statistics-every-ms";
    private final static String SETTING_MAX_QUEUE_SIZE = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".max-queue-size";
    private final static String SETTING_PRIORITIES_COUNT = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".priorities-count";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private StandardSettingsController standardSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.standardSettingsController.loadIntegerSetting(SETTING_WAIT_ON_STOP_MS, StandardExecutor.WAIT_ON_STOP_MS_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_WAIT_ON_RESTART_MS, StandardExecutor.WAIT_ON_RESTART_MS_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_MIN_TIME_PER_ITERATION_MS, StandardExecutor.MIN_TIME_PER_ITERATION_MS_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_TIMOUT_RUNNER_LIFE_MS, StandardExecutor.TIMEOUT_RUNNER_LIFE_MS_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_PRINT_STATISTICS_EVERY_MS, StandardExecutor.PRINT_STATISTICS_EVERY_MS_DEFAULT);

        this.standardSettingsController.loadIntegerSetting(SETTING_PRINT_QUEUE_STATISTICS_EVERY_MS, StandardQueue.PRINT_STATISTICS_EVERY_MS_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_MAX_QUEUE_SIZE, StandardQueue.MAX_QUEUE_SIZE_DEFAULT);
        this.standardSettingsController.loadIntegerSetting(SETTING_PRIORITIES_COUNT, StandardQueue.PRIORITIES_COUNT_DEFAULT);
    }

    public int printStatisticsEveryMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_PRINT_QUEUE_STATISTICS_EVERY_MS);
    }

    public int maxQueueSize() {
        return this.standardSettingsController.getIntegerSetting(SETTING_MAX_QUEUE_SIZE);
    }

    public int prioritiesCount() {
        return this.standardSettingsController.getIntegerSetting(SETTING_PRIORITIES_COUNT);
    }

    @Override
    public int getWaitOnStopMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_WAIT_ON_STOP_MS);
    }

    @Override
    public int getWaitOnRestartMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_WAIT_ON_RESTART_MS);
    }

    @Override
    public int getMinTimePerIterationMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_MIN_TIME_PER_ITERATION_MS);
    }

    @Override
    public int getTimeoutRunnerLifeMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_TIMOUT_RUNNER_LIFE_MS);
    }

    @Override
    public int getPrintStatisticsEveryMs() {
        return this.standardSettingsController.getIntegerSetting(SETTING_PRINT_STATISTICS_EVERY_MS);
    }
}

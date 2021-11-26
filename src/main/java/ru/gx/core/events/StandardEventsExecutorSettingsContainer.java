package ru.gx.core.events;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.core.settings.UnknownApplicationSettingException;
import ru.gx.core.worker.AbstractWorkerSettingsContainer;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
public class StandardEventsExecutorSettingsContainer extends AbstractWorkerSettingsContainer {
    public final static String STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX = "service.events.standard-executor";
    public static final String STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX = "service.events.standard-queue";

    private final static String SETTING_WAIT_ON_STOP_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_STOP_MS;
    private final static String SETTING_WAIT_ON_RESTART_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_RESTART_MS;
    private final static String SETTING_MIN_TIME_PER_ITERATION_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.MIN_TIME_PER_ITERATION_MS;
    private final static String SETTING_TIMOUT_RUNNER_LIFE_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.TIMEOUT_RUNNER_LIFE_MS;
    private final static String SETTING_PRINT_STATISTICS_EVERY_MS = STANDARD_EVENTS_EXECUTOR_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.PRINT_STATISTICS_EVERY_MS;

    private final static String SETTING_PRINT_STATISTICS_EVEY_MS = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".print-statistics-every-ms";
    private final static String SETTING_MAX_QUEUE_SIZE = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".max-queue-size";
    private final static String SETTING_PRIORITIES_COUNT = STANDARD_EVENTS_QUEUE_SETTINGS_PREFIX + ".priorities-count";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private StandardSettingsController simpleSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.simpleSettingsController.loadIntegerSetting(SETTING_WAIT_ON_STOP_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_WAIT_ON_RESTART_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_MIN_TIME_PER_ITERATION_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_TIMOUT_RUNNER_LIFE_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_PRINT_STATISTICS_EVERY_MS);

        this.simpleSettingsController.loadIntegerSetting(SETTING_PRINT_STATISTICS_EVEY_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_MAX_QUEUE_SIZE);
        this.simpleSettingsController.loadIntegerSetting(SETTING_PRIORITIES_COUNT);
    }

    public int printStatisticsEveryMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_PRINT_STATISTICS_EVEY_MS);
    }

    public int maxQueueSize() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_MAX_QUEUE_SIZE);
    }

    public int prioritiesCount() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_PRIORITIES_COUNT);
    }

    @Override
    public int getWaitOnStopMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_WAIT_ON_STOP_MS);
    }

    @Override
    public int getWaitOnRestartMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_WAIT_ON_RESTART_MS);
    }

    @Override
    public int getMinTimePerIterationMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_MIN_TIME_PER_ITERATION_MS);
    }

    @Override
    public int getTimeoutRunnerLifeMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_TIMOUT_RUNNER_LIFE_MS);
    }

    @Override
    public int getPrintStatisticsEveryMs() {
        return this.simpleSettingsController.getIntegerSetting(SETTING_PRINT_STATISTICS_EVERY_MS);
    }
}

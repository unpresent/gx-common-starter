package ru.gx.simpleworker;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.settings.StandardSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;
import ru.gx.worker.AbstractWorkerSettingsContainer;
import ru.gx.worker.WorkerSettingsContainer;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
public class SimpleWorkerSettingsContainer extends AbstractWorkerSettingsContainer {
    public final static String SIMPLE_WORKER_SETTINGS_PREFIX = "service.simple-worker";
    private final static String SETTING_WAIT_ON_STOP_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_STOP_MS;
    private final static String SETTING_WAIT_ON_RESTART_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.WAIT_ON_RESTART_MS;
    private final static String SETTING_MIN_TIME_PER_ITERATION_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.MIN_TIME_PER_ITERATION_MS;
    private final static String SETTING_TIMOUT_RUNNER_LIFE_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.TIMEOUT_RUNNER_LIFE_MS;
    private final static String SETTING_PRINT_STATISTICS_EVERY_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorkerSettingsContainer.PRINT_STATISTICS_EVERY_MS;

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

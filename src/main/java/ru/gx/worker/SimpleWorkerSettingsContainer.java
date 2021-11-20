package ru.gx.worker;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.settings.StandardSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
public class SimpleWorkerSettingsContainer {
    public final static String SIMPLE_WORKER_SETTINGS_PREFIX = "service.simple-worker";
    private final static String SETTING_SUFFIX_WAIT_ON_STOP_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorker.WAIT_ON_STOP_MS;
    private final static String SETTING_SUFFIX_WAIT_ON_RESTART_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorker.WAIT_ON_RESTART_MS;
    private final static String SETTING_SUFFIX_MIN_TIME_PER_ITERATION_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorker.MIN_TIME_PER_ITERATION_MS;
    private final static String SETTING_SUFFIX_TIMOUT_RUNNER_LIFE_MS = SIMPLE_WORKER_SETTINGS_PREFIX + "." + AbstractWorker.TIMEOUT_RUNNER_LIFE_MS;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private StandardSettingsController simpleSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.simpleSettingsController.loadIntegerSetting(SETTING_SUFFIX_WAIT_ON_STOP_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_SUFFIX_WAIT_ON_RESTART_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_SUFFIX_MIN_TIME_PER_ITERATION_MS);
        this.simpleSettingsController.loadIntegerSetting(SETTING_SUFFIX_TIMOUT_RUNNER_LIFE_MS);
    }
}

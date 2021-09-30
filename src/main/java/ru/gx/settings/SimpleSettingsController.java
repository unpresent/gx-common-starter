package ru.gx.settings;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
public class SimpleSettingsController extends AbstractSettingsController {
    String WAIT_ON_STOP_MS = "wait_on_stop_ms";
    String WAIT_ON_RESTART_MS = "wait_on_restarts_ms";
    String MIN_TIME_PER_ITERATION_MS = "min_time_per_iteration_ms";
    String TIMEOUT_RUNNER_LIFE_MS = "timeout_runner_life_ms";

    @Getter
    @NotNull
    private final String serviceName;

    public SimpleSettingsController(@NotNull String serviceName) {
        super();
        this.serviceName = serviceName;
    }

    @Override
    public void loadStringSetting(@NotNull final String settingName) throws UnknownApplicationSettingException {
        super.loadStringSetting(settingName);
    }

    @Override
    public void loadIntegerSetting(@NotNull final String settingName) throws UnknownApplicationSettingException {
        super.loadIntegerSetting(settingName);
    }

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.loadIntegerSetting(this.serviceName + "." + WAIT_ON_STOP_MS);
        this.loadIntegerSetting(this.serviceName + "." + WAIT_ON_RESTART_MS);
        this.loadIntegerSetting(this.serviceName + "." + MIN_TIME_PER_ITERATION_MS);
        this.loadIntegerSetting(this.serviceName + "." + TIMEOUT_RUNNER_LIFE_MS);
    }
}

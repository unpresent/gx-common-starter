package ru.gx.settings;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.worker.AbstractWorker;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
public class SimpleWorkerSettingsContainer {
    private final static String settingSuffixWaitOnStopMs = "service.simple-worker." + AbstractWorker.WAIT_ON_STOP_MS;
    private final static String settingSuffixWaitOnRestartMs = "service.simple-worker." + AbstractWorker.WAIT_ON_RESTART_MS;
    private final static String settingSuffixMinTimePerIterationMs = "service.simple-worker." + AbstractWorker.MIN_TIME_PER_ITERATION_MS;
    private final static String settingSuffixTimoutRunnerLifeMs = "service.simple-worker." + AbstractWorker.TIMEOUT_RUNNER_LIFE_MS;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private SimpleSettingsController simpleSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.simpleSettingsController.loadIntegerSetting(settingSuffixWaitOnStopMs);
        this.simpleSettingsController.loadIntegerSetting(settingSuffixWaitOnRestartMs);
        this.simpleSettingsController.loadIntegerSetting(settingSuffixMinTimePerIterationMs);
        this.simpleSettingsController.loadIntegerSetting(settingSuffixTimoutRunnerLifeMs);
    }
}

package ru.gx.settings;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import ru.gx.worker.AbstractWorker;

import javax.annotation.PostConstruct;

@SuppressWarnings("unused")
public class SimpleSettingsController extends AbstractSettingsController {
    @Getter
    @NotNull
    private final String serviceName;

    public SimpleSettingsController(@NotNull final String serviceName) {
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
        this.loadIntegerSetting(this.serviceName + "." + AbstractWorker.settingSuffixWaitOnStopMs);
        this.loadIntegerSetting(this.serviceName + "." + AbstractWorker.settingSuffixWaitOnRestartMs);
        this.loadIntegerSetting(this.serviceName + "." + AbstractWorker.settingSuffixMinTimePerIterationMs);
        this.loadIntegerSetting(this.serviceName + "." + AbstractWorker.settingSuffixTimoutRunnerLifeMs);
    }
}

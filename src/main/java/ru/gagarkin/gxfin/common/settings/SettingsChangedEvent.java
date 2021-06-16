package ru.gagarkin.gxfin.common.settings;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class SettingsChangedEvent extends ApplicationEvent {
    @Getter
    private String settingName;

    public SettingsChangedEvent(Object source, String settingName) {
        super(source);
        reset(settingName);
    }

    SettingsChangedEvent reset(String settingName) {
        this.settingName = settingName;
        return this;
    }
}

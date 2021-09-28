package ru.gx.common.settings;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

public class SettingsChangedEvent extends ApplicationEvent {
    @Getter
    private String settingName;

    public SettingsChangedEvent(Object source, @NotNull String settingName) {
        super(source);
        reset(settingName);
    }

    SettingsChangedEvent reset(@NotNull String settingName) {
        this.settingName = settingName;
        return this;
    }
}

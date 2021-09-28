package ru.gx.settings;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;

public class SettingsChangedEvent extends ApplicationEvent {
    @Getter
    @NotNull
    private String settingName;

    public SettingsChangedEvent(@NotNull final Object source, @NotNull String settingName) {
        super(source);
        this.settingName = settingName;
    }

    SettingsChangedEvent reset(@NotNull final String settingName) {
        this.settingName = settingName;
        return this;
    }
}

package ru.gx.core.settings;

import org.jetbrains.annotations.NotNull;

public class UnknownApplicationSettingException extends Exception {
    public UnknownApplicationSettingException(@NotNull final String settingName) {
        super("Unknown application setting " + settingName);
    }
}

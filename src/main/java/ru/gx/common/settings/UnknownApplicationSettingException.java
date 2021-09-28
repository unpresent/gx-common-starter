package ru.gx.common.settings;

import org.jetbrains.annotations.NotNull;

public class UnknownApplicationSettingException extends Exception {
    public UnknownApplicationSettingException(@NotNull final String settingName) {
        super("Unknown application setting " + settingName);
    }
}

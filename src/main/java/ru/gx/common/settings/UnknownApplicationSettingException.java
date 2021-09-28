package ru.gx.common.settings;

public class UnknownApplicationSettingException extends Exception {
    public UnknownApplicationSettingException(String settingName) {
        super("Unknown application setting " + settingName);
    }
}

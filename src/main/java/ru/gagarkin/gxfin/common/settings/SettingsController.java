package ru.gagarkin.gxfin.common.settings;

public interface SettingsController {
    Object getSetting(String settingName);

    void setSetting(String settingName, Object value);
}

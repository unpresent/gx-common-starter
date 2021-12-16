package ru.gx.core.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс контроллера настроек
 */
public interface SettingsController {
    /**
     * Получение настройки по названию
     * @param settingName название настройки
     * @return значение настройки
     */
    @SuppressWarnings("unused")
    @Nullable
    Object getSetting(@NotNull final String settingName);

    @SuppressWarnings("unused")
    @NotNull
    Integer getIntegerSetting(@NotNull final String settingName) throws ClassCastException;

    @SuppressWarnings("unused")
    @NotNull
    String getStringSetting(@NotNull final String settingName) throws ClassCastException;

    /**
     * Установка нового значения настойки. Если значение изменяется, то бросается событие об изменении.
     * @param settingName название настройки.
     * @param value значение настройки.
     */
    void setSetting(@NotNull final String settingName, @Nullable final Object value);
}

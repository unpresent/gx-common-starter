package ru.gx.common.settings;

import org.jetbrains.annotations.NotNull;

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
    Object getSetting(@NotNull String settingName);

    /**
     * Установка новго значения настойки. Если значение изменяется, то бросается событие об изменении.
     * @param settingName название настйроки
     * @param value значение настройки
     */
    void setSetting(@NotNull String settingName, Object value);
}

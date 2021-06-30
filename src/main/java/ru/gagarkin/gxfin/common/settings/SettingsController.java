package ru.gagarkin.gxfin.common.settings;

/**
 * Интерфейс контроллера настроек
 */
public interface SettingsController {
    /**
     * Получение настройки по названию
     * @param settingName название настройки
     * @return значение настройки
     */
    Object getSetting(String settingName);

    /**
     * Установка новго значения настойки. Если значение изменяется, то бросается событие об изменении.
     * @param settingName название настйроки
     * @param value значение настройки
     */
    void setSetting(String settingName, Object value);
}

package ru.gxfin.common.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Базовая реализация контроллера настроек.
 */
@Slf4j
public abstract class AbstractSettingsController implements SettingsController {
    /**
     * Передается в поле settingName объекта-события об изменении настройки, если поменялось слишком много настроек.
     */
    public static final String ALL = "*";

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationContext context;

    @Getter(AccessLevel.PROTECTED)
    private final Environment environment;

    /**
     * Список насроек со значениями
     */
    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Object> settings;

    /**
     * Объект-событие об изменении настройки/настроек
     */
    @Getter(AccessLevel.PROTECTED)
    private final SettingsChangedEvent settingsChangedEvent;

    public AbstractSettingsController(@NotNull ApplicationContext context) {
        super();
        this.context = context;
        this.environment = context.getEnvironment();
        this.settings = new HashMap<>();
        this.settingsChangedEvent = createSettingsChangedEvent();
    }

    /**
     * @return Создается объект-событие, которое будет использоваться для вызова события об изменении настройки/настроек.
     * Можно переопределять в наследниках, чтобы объект-событие был наследником от SettingsChangedEvent
     * @see SettingsChangedEvent
     */
    protected SettingsChangedEvent createSettingsChangedEvent() {
        return new SettingsChangedEvent(this, ALL);
    }

    /**
     * Получение значения настройки по ее имени.
     *
     * @param settingName имя настройки.
     * @return значение настройки.
     */
    @Override
    public Object getSetting(@NotNull String settingName) {
        return this.settings.get(settingName);
    }

    /**
     * Изменение значения настройки.
     *
     * @param settingName имя настройки.
     * @param value       новое значение настройки.
     */
    @Override
    public void setSetting(@NotNull String settingName, Object value) {
        final var oldValue = this.settings.get(settingName);
        if ((oldValue == null && value != null) || (oldValue != null && !oldValue.equals(value))) {
            log.info("setSetting({}, {})", settingName, value);
            this.settings.put(settingName, value);
            log.info("publishEvent(SettingsChangedEvent({}))", settingName);
            context.publishEvent(this.settingsChangedEvent.reset(settingName));
        }
    }

    @SuppressWarnings("unused")
    protected void loadStringSetting(@NotNull String settingName) throws UnknownApplicationSettingException {
        final var settingValue = this.getEnvironment().getProperty(settingName);
        if (settingValue == null) {
            throw new UnknownApplicationSettingException(settingName);
        }
        setSetting(settingName, settingValue);
    }

    @SuppressWarnings("unused")
    protected void loadIntegerSetting(@NotNull String settingName) throws UnknownApplicationSettingException {
        final var settingValue = this.getEnvironment().getProperty(settingName);
        if (settingValue == null) {
            throw new UnknownApplicationSettingException(settingName);
        }
        setSetting(settingName, Integer.parseInt(settingValue));
    }
}
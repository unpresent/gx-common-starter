package ru.gx.settings;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.*;

/**
 * Базовая реализация контроллера настроек.
 */
@Slf4j
public abstract class AbstractSettingsController implements SettingsController, ApplicationContextAware {
    /**
     * Передается в поле settingName объекта-события об изменении настройки, если поменялось слишком много настроек.
     */
    public static final String ALL = "*";

    @Getter
    private ApplicationContext applicationContext;

    @Getter(PROTECTED)
    private Environment environment;

    @Override
    public void setApplicationContext(@NotNull final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
    }

    /**
     * Список настроек со значениями
     */
    @Getter(PROTECTED)
    @NotNull
    private final Map<String, Object> settings;

    /**
     * Объект-событие об изменении настройки/настроек
     */
    @Getter(PROTECTED)
    @NotNull
    private final SettingsChangedEvent settingsChangedEvent;

    protected AbstractSettingsController() {
        super();
        this.settings = new HashMap<>();
        this.settingsChangedEvent = createSettingsChangedEvent();
    }

    /**
     * @return Создается объект-событие, которое будет использоваться для вызова события об изменении настройки/настроек.
     * Можно переопределять в наследниках, чтобы объект-событие был наследником от SettingsChangedEvent-а.
     * @see SettingsChangedEvent
     */
    @NotNull
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
    @Nullable
    public Object getSetting(@NotNull final String settingName) {
        return this.settings.get(settingName);
    }

    @Override
    @NotNull
    public Integer getIntegerSetting(@NotNull final String settingName) throws ClassCastException {
        final var value = getSetting(settingName);
        if (value == null) {
            throw new ClassCastException("Can't get setting " + settingName + " as Integer. Setting is null!");
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new ClassCastException("Can't get setting " + settingName + " as Integer. Setting class is " + value.getClass().getSimpleName());
    }

    @Override
    @NotNull
    public String getStringSetting(@NotNull final String settingName) throws ClassCastException {
        final var value = getSetting(settingName);
        if (value == null) {
            throw new ClassCastException("Can't get setting " + settingName + " as Integer. Setting is null!");
        }
        if (value instanceof String) {
            return (String) value;
        }
        throw new ClassCastException("Can't get setting " + settingName + " as String. Setting class is " + value.getClass().getSimpleName());
    }

    /**
     * Изменение значения настройки.
     *
     * @param settingName имя настройки.
     * @param value       новое значение настройки.
     */
    @Override
    public void setSetting(@NotNull final String settingName, @Nullable final Object value) {
        final var oldValue = this.settings.get(settingName);
        if ((oldValue == null && value != null) || (oldValue != null && !oldValue.equals(value))) {
            log.info("setSetting({}, {})", settingName, value);
            this.settings.put(settingName, value);
            log.info("publishEvent(SettingsChangedEvent({}))", settingName);
            this.applicationContext.publishEvent(this.settingsChangedEvent.reset(settingName));
        }
    }

    public void loadStringSetting(@NotNull final String settingName) throws UnknownApplicationSettingException {
        final var settingValue = this.getEnvironment().getProperty(settingName);
        if (settingValue == null) {
            throw new UnknownApplicationSettingException(settingName);
        }
        setSetting(settingName, settingValue);
    }

    public void loadIntegerSetting(@NotNull String settingName) throws UnknownApplicationSettingException {
        final var settingValue = this.getEnvironment().getProperty(settingName);
        if (settingValue == null) {
            throw new UnknownApplicationSettingException(settingName);
        }
        setSetting(settingName, Integer.parseInt(settingValue));
    }
}
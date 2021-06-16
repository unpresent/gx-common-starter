package ru.gagarkin.gxfin.common.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AbstractSettingsController implements SettingsController {
    public static final String ALL = "*";

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationContext context;

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Object> settings;

    @Getter(AccessLevel.PROTECTED)
    private final SettingsChangedEvent settingsChangedEvent;

    public AbstractSettingsController(ApplicationContext context) {
        super();
        this.context = context;
        this.settings = new HashMap<>();
        this.settingsChangedEvent = new SettingsChangedEvent(this, ALL);
    }

    @Override
    public Object getSetting(String settingName) {
        return this.settings.get(settingName);
    }

    @Override
    public void setSetting(String settingName, Object value) {
        var oldValue = this.settings.get(settingName);
        if ((oldValue == null && value != null) || (!oldValue.equals(value))) {
            log.info("setSetting({}, {})", settingName, value);
            this.settings.put(settingName, value);
            log.info("publishEvent(SettingsChangedEvent({}))", settingName);
            context.publishEvent(this.settingsChangedEvent.reset(settingName));
        }
    }
}

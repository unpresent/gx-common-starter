package ru.gx.core.settings;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

@SuppressWarnings("unused")
public class StandardSettingsController extends AbstractSettingsController {
    public final static String STANDARD_SETTINGS_CONTROLLER_PREFIX = "service.standard-settings-controller";

    public StandardSettingsController(@NotNull final ApplicationEventPublisher eventPublisher, @NotNull final Environment environment) {
        super(eventPublisher, environment);
    }
}

package ru.gx.common.settings;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("unused")
public class SimpleSettingsController extends AbstractSettingsController {
    public SimpleSettingsController(@NotNull ApplicationContext context) {
        super(context);
    }
}

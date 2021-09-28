package ru.gx.common.worker;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import ru.gx.common.settings.SettingsController;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public SimpleWorker(@NotNull String name, @NotNull ApplicationContext context, @NotNull SettingsController settingsController) {
        super(name, context, settingsController);
    }

    @Override
    protected AbstractIterationExecuteEvent createIterationExecuteEvent() {
        return new SimpleIterationExecuteEvent(this);
    }

    @Override
    protected AbstractStartingExecuteEvent createStartingExecuteEvent() {
        return new SimpleStartingExecuteEvent(this);
    }

    @Override
    protected AbstractStoppingExecuteEvent createStoppingExecuteEvent() {
        return new SimpleStoppingExecuteEvent(this);
    }
}

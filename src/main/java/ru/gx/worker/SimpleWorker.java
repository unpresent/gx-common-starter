package ru.gx.worker;

import org.jetbrains.annotations.NotNull;
import ru.gx.settings.SettingsController;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public SimpleWorker(@NotNull String name, @NotNull SettingsController settingsController) {
        super(name, settingsController);
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

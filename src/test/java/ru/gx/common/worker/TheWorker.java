package ru.gx.common.worker;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import ru.gx.common.settings.SettingsController;

@Slf4j
public class TheWorker extends SimpleWorker {
    private final int[] iterationsTimes = new int[] {100, 500, 1000, 2000, 5000, 11000, 12000, 1000};

    public TheWorker(String name, ApplicationContext context, SettingsController settingsController) {
        super(name, context, settingsController);
    }

    @Override
    protected int getMinTimePerIterationMs() {
        return 100;
    }

    @Override
    protected int getTimoutRunnerLifeMs() {
        return 10000;
    }

    @Override
    public int getWaitOnStopMs() {
        return 2000;
    }

    @Override
    public int getWaitOnRestartMs() {
        return 20000;
    }

    @EventListener(SimpleIterationExecuteEvent.class)
    public void iterationExecute(@NotNull final SimpleIterationExecuteEvent event) {
        log.debug("Starting iterationExecute()");
        try {
            runnerIsLifeSet();
            int iterationIndex = 0;
            final var wait = iterationIndex < this.iterationsTimes.length
                    ? this.iterationsTimes[iterationIndex]
                    : 250;
            Thread.sleep(wait);
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    private void internalTreatmentExceptionOnDataRead(@NotNull final SimpleIterationExecuteEvent event, Exception e) {
        log.error("", e);
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }
}

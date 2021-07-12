package worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import ru.gxfin.common.worker.AbstractIterationExecuteEvent;
import ru.gxfin.common.worker.AbstractStartingExecuteEvent;
import ru.gxfin.common.worker.AbstractStoppingExecuteEvent;
import ru.gxfin.common.worker.AbstractWorker;

@Slf4j
public class TheWorker extends AbstractWorker {
    private int iterationIndex = 0;
    private final int[] iterationsTimes = new int[] {100, 500, 1000, 2000, 5000, 11000, 12000, 1000};

    public TheWorker(String name) {
        super(name);
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
    public int getWaitOnStopMS() {
        return 2000;
    }

    @Override
    public int getWaitOnRestartMS() {
        return 20000;
    }

    @Override
    protected AbstractIterationExecuteEvent createIterationExecuteEvent() {
        return new TheIterationExecuteEvent(this);
    }

    @Override
    protected AbstractStartingExecuteEvent createStartingExecuteEvent() {
        return new TheStartingExecuteEvent(this);
    }

    @Override
    protected AbstractStoppingExecuteEvent createStoppingExecuteEvent() {
        return new TheStoppingExecuteEvent(this);
    }


    @EventListener(TheIterationExecuteEvent.class)
    public void iterationExecute(TheIterationExecuteEvent event) {
        log.debug("Starting iterationExecute()");
        try {
            runnerIsLifeSet();
            final var wait = this.iterationIndex < this.iterationsTimes.length
                    ? this.iterationsTimes[this.iterationIndex]
                    : 250;
            Thread.sleep(wait);
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    private void internalTreatmentExceptionOnDataRead(TheIterationExecuteEvent event, Exception e) {
        log.error(e.getMessage());
        log.error(e.getStackTrace().toString());
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }
}

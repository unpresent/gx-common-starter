package ru.gx.events;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gx.worker.*;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class StandardEventsExecutor extends AbstractWorker {
    public static final String WORKER_DEFAULT_NAME = "standard-events-executor";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private EventsPrioritizedQueue eventsQueue;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private StandardEventsExecutorSettingsContainer settingsContainer;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private StandardEventsExecutorStatisticsInfo statisticsInfo;

    private final EventsProcessor eventsProcessor;

    private final OnIterationExecuteEventInternal iterationExecuteEvent;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private StandardEventsExecutorOnStartingExecuteEvent onStartingExecuteEvent;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private StandardEventsExecutorOnStoppingExecuteEvent onStoppingExecuteEvent;

    public StandardEventsExecutor(@NotNull final String name) {
        super(name);
        this.eventsProcessor = new EventsProcessorInternal();
        this.iterationExecuteEvent = new OnIterationExecuteEventInternal(this);
    }

    @Override
    public AbstractOnIterationExecuteEvent iterationExecuteEvent() {
        return this.iterationExecuteEvent;
    }

    @Override
    public AbstractOnStartingExecuteEvent startingExecuteEvent() {
        return this.onStartingExecuteEvent;
    }

    @Override
    public AbstractOnStoppingExecuteEvent stoppingExecuteEvent() {
        return this.onStoppingExecuteEvent;
    }

    protected static class EventsProcessorInternal extends AbstractEventsProcessor {
        protected EventsProcessorInternal() {
            super();
        }
    }

    protected static class OnIterationExecuteEventInternal extends AbstractOnIterationExecuteEvent {
        public OnIterationExecuteEventInternal(@NotNull Object source) {
            super(source);
        }
    }

    @EventListener(OnIterationExecuteEventInternal.class)
    public void iterationExecute(@NotNull final OnIterationExecuteEventInternal iterationEvent) {
        log.debug("Starting iterationExecute()");
        try {
            this.runnerIsLifeSet();
            final var publishStatisticsEveryMs = this.settingsContainer.printStatisticsEveryMs();
            iterationEvent.setImmediateRunNextIteration(false);

            Event dataEvent = null;
            this.statisticsInfo.eventExecuteStarting();
            try {
                dataEvent = this.eventsProcessor.pollAndProcessEvent(this.eventsQueue);
                if (dataEvent != null) {
                    iterationEvent.setImmediateRunNextIteration(true);
                }
            } finally {
                if (dataEvent != null) {
                    this.statisticsInfo.eventExecuteFinished(dataEvent);
                }
            }

            if (this.statisticsInfo.lastResetMsAgo() > publishStatisticsEveryMs) {
                log.info(this.statisticsInfo.getInfoForLog());
                this.statisticsInfo.reset();
            }
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(iterationEvent, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    /**
     * Обработка ошибки при выполнении итерации.
     *
     * @param event Объект-событие с параметрами итерации.
     * @param e     Ошибка, которую требуется обработать.
     */
    private void internalTreatmentExceptionOnDataRead(OnIterationExecuteEventInternal event, Exception e) {
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

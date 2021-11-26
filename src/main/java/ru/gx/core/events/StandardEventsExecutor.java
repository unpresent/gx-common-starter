package ru.gx.core.events;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.gx.core.worker.AbstractWorker;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;
import ru.gx.core.worker.AbstractOnIterationExecuteEvent;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class StandardEventsExecutor extends AbstractWorker {
    public static final String WORKER_DEFAULT_NAME = "standard-events-executor";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private EventsPrioritizedQueue eventsQueue;

    @NotNull
    private final EventsProcessor eventsProcessor;

    @Getter
    private final OnIterationExecuteEventInternal iterationExecuteEvent;

    @Getter
    @NotNull
    private final StandardEventsExecutorOnStartingExecuteEvent startingExecuteEvent;

    @Getter
    @NotNull
    private final StandardEventsExecutorOnStoppingExecuteEvent stoppingExecuteEvent;

    public StandardEventsExecutor(
            @NotNull final String name,
            @NotNull final StandardEventsExecutorSettingsContainer settingsContainer,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MeterRegistry meterRegistry
    ) {
        super(name, settingsContainer, meterRegistry);
        this.eventsProcessor = new EventsProcessorInternal(eventPublisher, (StandardEventsExecutorStatisticsInfo)this.getStatisticsInfo());
        this.iterationExecuteEvent = new OnIterationExecuteEventInternal(this);
        this.startingExecuteEvent = new StandardEventsExecutorOnStartingExecuteEvent(this);
        this.stoppingExecuteEvent = new StandardEventsExecutorOnStoppingExecuteEvent(this);
    }

    protected static class EventsProcessorInternal extends AbstractEventsProcessor {
        protected EventsProcessorInternal(@NotNull final ApplicationEventPublisher eventPublisher, @NotNull final StandardEventsExecutorStatisticsInfo eventsStatisticsInfo) {
            super(eventPublisher, eventsStatisticsInfo);
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
            iterationEvent.setImmediateRunNextIteration(false);

            Event dataEvent = this.eventsProcessor.pollEvent(this.eventsQueue);
            if (dataEvent != null) {
                this.eventsProcessor.processEvent(dataEvent);
            }
            if (dataEvent != null || this.getEventsQueue().queueSize() > 0) {
                iterationEvent.setImmediateRunNextIteration(true);
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

    @EventListener(DoStartStandardEventsExecutorEvent.class)
    public void doStartSimpleWorkerEvent(DoStartStandardEventsExecutorEvent __) {
        this.start();
    }

    @EventListener(DoStopStandardEventsExecutorEvent.class)
    public void doStopSimpleWorkerEvent(DoStopStandardEventsExecutorEvent __) {
        this.stop();
    }

    @Override
    protected AbstractWorkerStatisticsInfo createStatisticsInfo() {
        return new StandardEventsExecutorStatisticsInfo(this, this.getMeterRegistry());
    }
}

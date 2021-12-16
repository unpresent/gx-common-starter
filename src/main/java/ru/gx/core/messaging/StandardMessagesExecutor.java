package ru.gx.core.messaging;

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
public class StandardMessagesExecutor extends AbstractWorker {
    public static final String WORKER_DEFAULT_NAME = "standard-messages-executor";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private MessagesPrioritizedQueue messagesQueue;

    @NotNull
    private final MessagesProcessor messagesProcessor;

    @Getter
    private final OnIterationExecuteEventInternal iterationExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStartingExecuteEvent startingExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStoppingExecuteEvent stoppingExecuteEvent;

    public StandardMessagesExecutor(
            @NotNull final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MeterRegistry meterRegistry
    ) {
        super(name, settingsContainer, meterRegistry);
        this.messagesProcessor = new MessagesProcessorInternal(eventPublisher, (StandardMessagesExecutorStatisticsInfo)this.getStatisticsInfo());
        this.iterationExecuteEvent = new OnIterationExecuteEventInternal(this);
        this.startingExecuteEvent = new StandardMessagesExecutorOnStartingExecuteEvent(this);
        this.stoppingExecuteEvent = new StandardMessagesExecutorOnStoppingExecuteEvent(this);
    }

    protected static class MessagesProcessorInternal extends AbstractMessagesProcessor {
        protected MessagesProcessorInternal(@NotNull final ApplicationEventPublisher eventPublisher, @NotNull final StandardMessagesExecutorStatisticsInfo executorStatisticsInfo) {
            super(eventPublisher, executorStatisticsInfo);
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

            final var message = this.messagesProcessor.pollMessage(this.messagesQueue);
            if (message != null) {
                this.messagesProcessor.processMessage(message);
            }
            if (message != null || this.getMessagesQueue().queueSize() > 0) {
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

    @SuppressWarnings("unused")
    @EventListener(DoStartStandardMessagesExecutorEvent.class)
    public void doStartSimpleWorkerEvent(DoStartStandardMessagesExecutorEvent __) {
        this.start();
    }

    @SuppressWarnings("unused")
    @EventListener(DoStopStandardMessagesExecutorEvent.class)
    public void doStopSimpleWorkerEvent(DoStopStandardMessagesExecutorEvent __) {
        this.stop();
    }

    @Override
    protected AbstractWorkerStatisticsInfo createStatisticsInfo() {
        return new StandardMessagesExecutorStatisticsInfo(this, this.getMeterRegistry());
    }
}

package ru.gx.core.messaging;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.gx.core.channels.OnErrorBehavior;
import ru.gx.core.worker.AbstractOnIterationExecuteEvent;
import ru.gx.core.worker.AbstractWorker;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public abstract class AbstractMessagesExecutor extends AbstractWorker {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    @Getter(PROTECTED)
    @NotNull
    private final MessagesPrioritizedQueue messagesQueue;

    @Getter
    private final OnIterationExecuteEventInternal iterationExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStartingExecuteEvent startingExecuteEvent;

    @Getter
    @NotNull
    private final StandardMessagesExecutorOnStoppingExecuteEvent stoppingExecuteEvent;

    @Override
    public StandardMessagesExecutorStatisticsInfo getStatisticsInfo() {
        return (StandardMessagesExecutorStatisticsInfo) super.getStatisticsInfo();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractMessagesExecutor(
            @NotNull final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull MessagesPrioritizedQueue messagesQueue
    ) {
        super(name, settingsContainer, meterRegistry, eventPublisher);
        this.messagesQueue = messagesQueue;
        this.iterationExecuteEvent = new OnIterationExecuteEventInternal(this);
        this.startingExecuteEvent = new StandardMessagesExecutorOnStartingExecuteEvent(this);
        this.stoppingExecuteEvent = new StandardMessagesExecutorOnStoppingExecuteEvent(this);
    }
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="static class OnIterationExecuteEventInternal">
    protected static class OnIterationExecuteEventInternal extends AbstractOnIterationExecuteEvent {
        public OnIterationExecuteEventInternal(@NotNull Object source) {
            super(source);
        }
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Iterations processing">
    @EventListener(OnIterationExecuteEventInternal.class)
    public void iterationExecute(@NotNull final OnIterationExecuteEventInternal iterationEvent) {
        log.debug("Starting iterationExecute()");
        try {
            this.runnerIsLifeSet();
            iterationEvent.setImmediateRunNextIteration(false);

            setCurrentExecutionInfo("Before internalPollMessage");
            final var message = this.internalPollMessage(this.messagesQueue);
            setCurrentExecutionInfo("After internalPollMessage");
            if (message != null) {
                this.internalProcessMessage(message);
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

    /**
     * Извлечь событие из контейнера очередей и обработать его (вызвать обработчик).
     *
     * @param queue Контейнер очередей.
     * @return True - событие было извлечено и обработано. False - нет событий в очереди.
     */
    protected Object internalPollMessage(
            @NotNull final MessagesPrioritizedQueue queue
    ) {
        final var event = queue.pollMessage();
        if (event == null) {
            log.debug("No messages in queue {}", queue.getName());
        } else {
            log.debug("Polled message " + event.getClass().getName());
        }
        return event;
    }

    /**
     * Обработка одного сообщения.
     *
     * @param message Сообщение, которое бросаем на обработку через this.eventPublisher.
     */
    protected void internalProcessMessage(@NotNull final Object message) {
        try {
            try {
                setCurrentExecutionInfo("Before publishEvent: " + message);
                this.getApplicationEventPublisher()
                        .publishEvent(message);
                setCurrentExecutionInfo("After publishEvent: " + message);
            } catch (Exception e) {
                log.error("", e);
                if (message instanceof final Message<?> typedMessage) {
                    final var channel = typedMessage.getChannelDescriptor();
                    if (channel.getOnErrorBehavior() == OnErrorBehavior.StopProcessOnError) {
                        channel.setBlockingError(e);
                    }
                    this.getMessagesQueue().returnErrorMessage(
                            typedMessage.getChannelDescriptor().getPriority(),
                            typedMessage
                    );
                }
            }
        } finally {
            if (message instanceof final Message<?> typedMessage) {
                this.getStatisticsInfo().messagesExecuteFinished(typedMessage);
            }
        }
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Start & Stop">
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
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

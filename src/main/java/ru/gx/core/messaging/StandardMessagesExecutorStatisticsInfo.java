package ru.gx.core.messaging;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("rawtypes")
public class StandardMessagesExecutorStatisticsInfo extends AbstractWorkerStatisticsInfo {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    /**
     * Размер очереди соощбщений
     */
    public static final String METRIC_EVENT_QUEUE_SIZE = "messages.queue.size";
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Метрика: Текущее количество сообщений в очереди сообщений.
     */
    @Getter(PROTECTED)
    @NotNull
    private final Gauge metricMessagesQueueSize;

    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public StandardMessagesExecutorStatisticsInfo(
            @NotNull final StandardMessagesExecutor worker,
            @NotNull final MeterRegistry meterRegistry
    ) {
        super(worker, meterRegistry);
        this.metricMessagesQueueSize = Gauge.builder(METRIC_EVENT_QUEUE_SIZE, this::getMessagesQueueSize)
                .tags(this.getMetricsTags())
                .register(this.getMeterRegistry());
    }

    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Getters">
    @Override
    protected @NotNull StandardMessagesExecutor getOwner() {
        return (StandardMessagesExecutor) super.getOwner();
    }

    private int getMessagesQueueSize() {
        return this.getOwner()
                .getMessagesQueue()
                .queueSize(); // Не переживаем, внутри  живет AtomicInteger:
    }

    @Override
    public String getPrintableInfo() {
        final var size = getMessagesQueueSize();
        return super.getPrintableInfo() + '\n' +
                "MessagesQueue.size = " + size;
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Logic">

    /**
     * Фиксируется факт обработки сообщения.
     *
     * @param message сообщение, которое было обработано.
     */
    public void messagesExecuteFinished(@NotNull final Message message) {
        final var channel = message.getChannelDescriptor();
        channel.recordMessageExecuted(getOwner().getWorkerName(), System.currentTimeMillis() - getLastIterationStartedMs());
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
}

package ru.gx.core.messaging;

import io.micrometer.core.instrument.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Статистики исполнения сообщений для каждого класса сообщений.
     */
    @Getter
    private final Map<Class<? extends Message>, MessagesExecuteStatistics> messagesStats = new HashMap<>();

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
        privateReset();
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
        final var str = new StringBuilder(super.getPrintableInfo());
        str.append('\n');
        str.append("Events queue.size = ");
        str.append(size);
        str.append("; Events stat is: {");
        var isFirst = true;
        for (var eventClass : this.messagesStats.keySet()) {
            final var eStat = this.messagesStats.get(eventClass);
            if (eStat.isEmpty) {
                continue;
            }

            if (!isFirst) {
                str.append("; ");
            }
            str.append(eventClass.getSimpleName());
            str.append(": count = ");
            str.append(eStat.getCount());
            str.append(", totalMs = ");
            str.append(eStat.getTotalTimeMs());
            str.append(", maxTime = ");
            str.append(eStat.getMaxTimeMsPerEvent());
            str.append(", avgTime = ");
            str.append(eStat.getCount() > 0 ? eStat.getTotalTimeMs() / eStat.getCount() : "NaN");
            isFirst = false;
        }
        str.append("}");
        return str.toString();
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
        final var eventClass = message.getClass();
        var curEventStat = this.messagesStats.get(eventClass);
        if (curEventStat == null) {
            curEventStat = new MessagesExecuteStatistics(eventClass, this.getOwner(), this.getMeterRegistry());
            this.messagesStats.put(eventClass, curEventStat);
        }
        curEventStat.setMessageExecuted(System.currentTimeMillis() - getLastIterationStartedMs());
    }

    /**
     * Собственно сброс метрик. <br/>
     * Вызывается в т.ч. и в конструкторе!
     */
    private void privateReset() {
        this.messagesStats.values().forEach(MessagesExecuteStatistics::reset);
    }

    @Override
    protected void internalReset() {
        super.internalReset();
        privateReset();
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="static class MessagesExecuteStatistics">

    @Getter
    public static class MessagesExecuteStatistics {

        public static final String METRIC_EVENT_MESSAGE_COUNT = "messages.count";
        public static final String METRIC_EVENT_MESSAGE_TIME_MS = "messages.time";

        public static final String METRIC_TAG_NAME_EVENT_CLASS = "message_class";
        public static final String METRIC_TAG_EXECUTOR = "executor";

        @NotNull
        private final StandardMessagesExecutor owner;

        @NotNull
        private final MeterRegistry meterRegistry;

        /**
         * Количество исполнений с момента последнего сброса.
         */
        private int count;

        /**
         * Общее время затраченное на обработку событий с момента последнего сброса.
         */
        private long totalTimeMs;

        /**
         * Максимальное время на исполнение одного события с момента последнего сброса.
         */
        private long maxTimeMsPerEvent;

        /**
         * Признак того, что с момента последнего сброса не было зафиксировано ни одной обработки события.
         */
        private boolean isEmpty;

        /**
         * Общие для всех метрик ярлыки.
         */
        @Getter(PROTECTED)
        private final List<Tag> metricsTags;

        /**
         * Метрика: количество исполнений с момента запуска.
         */
        @Getter(PROTECTED)
        @NotNull
        private final Counter metricExecutionsCount;

        /**
         * Метрика: общее время на исполнения с момента запуска.
         */
        @Getter(PROTECTED)
        @NotNull
        private final Timer metricExecutionsTime;

        public MessagesExecuteStatistics(
                @NotNull final Class<? extends Message> messageClass,
                @NotNull final StandardMessagesExecutor executor,
                @NotNull final MeterRegistry meterRegistry
        ) {
            super();
            this.owner = executor;
            this.meterRegistry = meterRegistry;
            this.metricsTags = List.of(
                    Tag.of(METRIC_TAG_EXECUTOR, executor.getWorkerName()),
                    Tag.of(METRIC_TAG_NAME_EVENT_CLASS, messageClass.getSimpleName())
            );
            this.metricExecutionsCount = Counter.builder(METRIC_EXECUTIONS_COUNT)
                    .tags(this.metricsTags)
                    .register(this.meterRegistry);
            this.metricExecutionsTime = Timer.builder(METRIC_EXECUTIONS_TIME)
                    .tags(this.metricsTags)
                    .register(this.meterRegistry);
            internalReset();
        }

        /**
         * Фиксируется факт обработки события.
         *
         * @param timeMs Время, затраченное на обработку события.
         */
        public void setMessageExecuted(long timeMs) {
            this.count++;
            this.totalTimeMs += timeMs;
            if (timeMs > this.maxTimeMsPerEvent) {
                this.maxTimeMsPerEvent = timeMs;
            }
            this.isEmpty = false;
        }

        public void reset() {
            internalReset();
        }

        protected void internalReset() {
            this.isEmpty = true;
            this.count = 0;
            this.totalTimeMs = 0;
            this.maxTimeMsPerEvent = 0;
        }
    }
    // </editor-fold">
    // -----------------------------------------------------------------------------------------------------------------
}

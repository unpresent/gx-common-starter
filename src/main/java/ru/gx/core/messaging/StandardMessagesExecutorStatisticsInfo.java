package ru.gx.core.messaging;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("rawtypes")
public class StandardMessagesExecutorStatisticsInfo extends AbstractWorkerStatisticsInfo {
    public static final String METRIC_EVENT_TIMER = "message-time";
    public static final String METRIC_EVENT_COUNTER = "message-count";

    public static final String METRIC_TAG_NAME_EVENT_CLASS = "message-class";

    @Getter
    private volatile long lastResetMs;

    @Getter
    private long eventStartedMs;

    @Getter
    private final Map<Class<? extends Message>, MessagesExecuteStatistics> messagesStats;

    public StandardMessagesExecutorStatisticsInfo(@NotNull final StandardMessagesExecutor worker, @NotNull final MeterRegistry meterRegistry) {
        super(worker, meterRegistry);
        this.messagesStats = new HashMap<>();
        this.privateClear();
    }

    public void messageExecuteStarting() {
        this.eventStartedMs = System.currentTimeMillis();
    }

    public void messagesExecuteFinished(@NotNull final Message message) {
        final var eventClass = message.getClass();
        var curEventStat = this.messagesStats.get(eventClass);
        if (curEventStat == null) {
            curEventStat = new MessagesExecuteStatistics(this.getMeterRegistry(), eventClass);
            this.messagesStats.put(eventClass, curEventStat);
        }
        curEventStat.setMessageExecuted(System.currentTimeMillis() - this.eventStartedMs);
    }

    @Override
    public String getPrintableInfo() {
        final var str = new StringBuilder(super.getPrintableInfo());
        str.append('\n');
        str.append("Events queue.size = ");
        str.append(((StandardMessagesExecutor)this.getWorker()).getMessagesQueue().queueSize());
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
            str.append(", minTime = ");
            str.append(eStat.getMinTimeMsPerEvent());
            str.append(", maxTime = ");
            str.append(eStat.getMaxTimeMsPerEvent());
            str.append(", avgTime = ");
            str.append(eStat.getCount() > 0 ? eStat.getTotalTimeMs() / eStat.getCount() : "NaN");
            isFirst = false;
        }
        str.append("}");
        return str.toString();
    }

    @Override
    public void reset() {
        privateClear();
        super.reset();
    }

    /**
     * Чтобы никто не переопределял. Вызывается в конструкторе!
     */
    private void privateClear() {
        this.messagesStats.values().forEach(MessagesExecuteStatistics::reset);
        this.lastResetMs = System.currentTimeMillis();
    }

    @Getter
    public static class MessagesExecuteStatistics {
        /**
         * Количество исполнений с момента последнего сброса.
         */
        private int count;

        /**
         * Общее время затраченное на обработку событий с момента последнего сброса.
         */
        private long totalTimeMs;

        /**
         * Минимальное время на исполнение одного события с момента последнего сброса.
         */
        private long minTimeMsPerEvent;

        /**
         * Максимальное время на исполнение одного события с момента последнего сброса.
         */
        private long maxTimeMsPerEvent;

        /**
         * Признак того, что с момента последнего сброса не было зафиксировано ни одной обработки события.
         */
        private boolean isEmpty;

        /**
         * Накопитель времен исполнений для отчета через actuator.
         * Не сбрасывается при reset().
         */
        @Getter(PROTECTED)
        private final Timer iterationTimer;

        /**
         * Накопитель количества исполнений для отчета через actuator.
         * Не сбрасывается при reset().
         */
        @Getter(PROTECTED)
        private final Counter iterationCounter;

        public MessagesExecuteStatistics(@NotNull final MeterRegistry meterRegistry, @NotNull final Class<? extends Message> messageClass) {
            super();
            this.iterationCounter = meterRegistry.counter(METRIC_EVENT_COUNTER, METRIC_TAG_NAME_EVENT_CLASS, messageClass.getSimpleName());
            this.iterationTimer = meterRegistry.timer(METRIC_EVENT_TIMER, METRIC_TAG_NAME_EVENT_CLASS, messageClass.getSimpleName());
            reset();
        }

        /**
         * Фиксируется факт обработки события.
         *
         * @param timeMs Время, затраченное на обработку события.
         */
        public void setMessageExecuted(long timeMs) {
            this.count++;
            this.totalTimeMs += timeMs;
            if (this.minTimeMsPerEvent < 0 || timeMs < this.minTimeMsPerEvent) {
                this.minTimeMsPerEvent = timeMs;
            }
            if (timeMs > this.maxTimeMsPerEvent) {
                this.maxTimeMsPerEvent = timeMs;
            }
            this.isEmpty = false;
        }

        public void reset() {
            this.isEmpty = true;
            this.count = 0;
            this.totalTimeMs = 0;
            this.minTimeMsPerEvent = -1;
            this.maxTimeMsPerEvent = 0;
        }
    }
}

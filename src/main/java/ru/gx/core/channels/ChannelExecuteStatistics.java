package ru.gx.core.channels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.StatisticsInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
public class ChannelExecuteStatistics {

    @Getter
    @NotNull
    @JsonIgnore
    private final ChannelHandlerDescriptor owner;

    @NotNull
    @JsonIgnore
    private final Map<String, ChannelWorkerExecuteStatistics> workerExecuteStatisticsMap = new HashMap<>();

    @Getter
    @NotNull
    @JsonIgnore
    private final MeterRegistry meterRegistry;

    @JsonProperty("channelName")
    public String getChannelName() {
        return owner.getChannelName();
    }

    @JsonProperty("byWorkersStat")
    public Iterable<ChannelWorkerExecuteStatistics> getChannelWorkerExecuteStatistics() {
        return this.workerExecuteStatisticsMap.values();
    }

    public ChannelExecuteStatistics(
            @NotNull final ChannelHandlerDescriptor owner,
            @NotNull final MeterRegistry meterRegistry
    ) {
        super();
        this.owner = owner;
        this.meterRegistry = meterRegistry;
        privateReset();
    }

    /**
     * Фиксируется факт обработки сообщения.
     *
     * @param timeMs Время, затраченное на обработку сообщения.
     */
    public void recordMessageExecuted(@NotNull final String workerName, long timeMs) {
        var stat = this.workerExecuteStatisticsMap.get(workerName);
        if (stat == null) {
            stat = new ChannelWorkerExecuteStatistics(this, workerName);
            this.workerExecuteStatisticsMap.put(workerName, stat);
        }

        stat.recordMessageExecuted(timeMs);
    }

    /**
     * Фиксируется факт обработки пакета сообщений.
     *
     * @param timeMs Время, затраченное на обработку сообщения.
     * @param count Количество сообщений в пакете.
     */
    public void recordMessagesExecuted(@NotNull final String workerName, final long timeMs, final int count) {
        var stat = this.workerExecuteStatisticsMap.get(workerName);
        if (stat == null) {
            stat = new ChannelWorkerExecuteStatistics(this, workerName);
            this.workerExecuteStatisticsMap.put(workerName, stat);
        }

        stat.recordMessagesExecuted(timeMs, count);
    }

    public void reset() {
        privateReset();
    }

    protected void privateReset() {
        this.workerExecuteStatisticsMap.values()
                .forEach(ChannelWorkerExecuteStatistics::reset);
    }

    public String getPrintableInfo() {
        final var str = new StringBuilder();
        str.append('<');
        str.append(owner.getChannelName());
        str.append('>');
        if (owner.isBlockedByError()) {
            str.append("/ERR/");
        }
        str.append(" by-workers: {");
        var isFirst = true;
        for (var workerName : this.workerExecuteStatisticsMap.keySet()) {
            final var stat = this.workerExecuteStatisticsMap.get(workerName);
            if (stat.isEmpty()) {
                continue;
            }

            if (!isFirst) {
                str.append("; ");
            }
            str.append(stat.getPrintableInfo());
            isFirst = false;
        }
        str.append("}");
        return str.toString();
    }

    @Getter
    public static class ChannelWorkerExecuteStatistics {

        @NotNull
        private final ChannelExecuteStatistics owner;

        @NotNull
        private final String workerName;

        @NotNull
        private final MeterRegistry meterRegistry;

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

        /**
         * Количество исполнений с момента последнего сброса.
         */
        private volatile int count;

        /**
         * Общее время затраченное на обработку событий с момента последнего сброса.
         */
        private volatile long totalTimeMs;

        /**
         * Максимальное время на исполнение одного события с момента последнего сброса.
         */
        private volatile long maxTimeMsPerEvent;

        /**
         * Признак того, что с момента последнего сброса не было зафиксировано ни одной обработки события.
         */
        private volatile boolean isEmpty;

        /**
         * Момент времени обработки последнего сообщения
         */
        @Getter(PROTECTED)
        private volatile long lastMessageProcessedMs;

        public ChannelWorkerExecuteStatistics(
                @NotNull final ChannelExecuteStatistics owner,
                @NotNull final String workerName
        ) {
            super();
            this.owner = owner;
            this.workerName = workerName;
            this.meterRegistry = owner.getMeterRegistry();
            this.metricsTags = List.of(
                    Tag.of(StatisticsInfo.METRIC_TAG_WORKER_NAME, workerName),
                    Tag.of(StatisticsInfo.METRIC_TAG_CHANNEL_NAME, owner.getOwner().getChannelName())
            );
            this.metricExecutionsCount = Counter.builder(StatisticsInfo.METRIC_EXECUTIONS_COUNT)
                    .tags(this.metricsTags)
                    .register(this.meterRegistry);
            this.metricExecutionsTime = Timer.builder(StatisticsInfo.METRIC_EXECUTIONS_TIME)
                    .tags(this.metricsTags)
                    .register(this.meterRegistry);

            privateReset();
        }

        public void reset() {
            privateReset();
        }

        private void privateReset() {
            synchronized (getOwner()) {
                this.isEmpty = true;
                this.count = 0;
                this.totalTimeMs = 0;
                this.maxTimeMsPerEvent = 0;
            }
        }

        /**
         * @return Сколько прошло миллисекунд с обработки последнего сообщения
         */
        public long getLastMessageLeftMs() {
            return System.currentTimeMillis() - this.lastMessageProcessedMs;
        }

        /**
         * Фиксируется факт обработки сообщения.
         *
         * @param timeMs Время, затраченное на обработку сообщения.
         */
        public void recordMessageExecuted(long timeMs) {
            synchronized (getOwner()) {
                this.lastMessageProcessedMs = System.currentTimeMillis();
                this.count++;
                this.totalTimeMs += timeMs;
                if (timeMs > this.maxTimeMsPerEvent) {
                    this.maxTimeMsPerEvent = timeMs;
                }
                this.isEmpty = false;
            }
        }

        /**
         * Фиксируется факт обработки пакета сообщений.
         *
         * @param timeMs Время, затраченное на обработку сообщения.
         * @param count  Количество сообщений в пакете.
         */
        public void recordMessagesExecuted(final long timeMs, final int count) {
            synchronized (getOwner()) {
                this.lastMessageProcessedMs = System.currentTimeMillis();
                this.count += count;
                this.totalTimeMs += timeMs;
                if (timeMs > this.maxTimeMsPerEvent) {
                    this.maxTimeMsPerEvent = timeMs;
                }
                this.isEmpty = false;
            }
        }

        public String getPrintableInfo() {
            return this.workerName +
                    ": lastMessage: " +
                    getLastMessageLeftMs() / 1000 +
                    " s, count = " +
                    getCount() +
                    ", totalMs = " +
                    getTotalTimeMs() +
                    ", maxTime = " +
                    getMaxTimeMsPerEvent() +
                    ", avgTime = " +
                    (getCount() > 0 ? getTotalTimeMs() / getCount() : "NaN");
        }

    }
}

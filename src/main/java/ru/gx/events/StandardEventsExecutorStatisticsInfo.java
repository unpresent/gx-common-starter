package ru.gx.events;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractWorkerStatisticsInfo;

import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

public class StandardEventsExecutorStatisticsInfo extends AbstractWorkerStatisticsInfo {
    public static final String METRIC_EVENT_TIMER = "event-time";
    public static final String METRIC_EVENT_COUNTER = "event-count";

    public static final String METRIC_TAG_NAME_EVENT_CLASS = "event-class";

    @Getter
    private volatile long lastResetMs;

    @Getter
    private long eventStartedMs;

    @Getter
    private final Map<Class<? extends Event>, EventsExecuteStatistics> eventsStats;

    public StandardEventsExecutorStatisticsInfo(@NotNull final StandardEventsExecutor worker, @NotNull final MeterRegistry meterRegistry) {
        super(worker, meterRegistry);
        this.eventsStats = new HashMap<>();
        this.privateClear();
    }

    public void eventExecuteStarting() {
        this.eventStartedMs = System.currentTimeMillis();
    }

    public void eventExecuteFinished(@NotNull final Event event) {
        final var eventClass = event.getClass();
        var curEventStat = this.eventsStats.get(eventClass);
        if (curEventStat == null) {
            curEventStat = new EventsExecuteStatistics(this.getMeterRegistry(), eventClass);
            this.eventsStats.put(eventClass, curEventStat);
        }
        curEventStat.setEventExecuted(System.currentTimeMillis() - this.eventStartedMs);
    }

    @Override
    public String getPrintableInfo() {
        final var str = new StringBuilder(super.getPrintableInfo());
        str.append('\n');
        str.append("Events queue.size = ");
        str.append(((StandardEventsExecutor)this.getWorker()).getEventsQueue().queueSize());
        str.append("; Events stat is: {");
        var isFirst = true;
        for (var eventClass : this.eventsStats.keySet()) {
            final var eStat = this.eventsStats.get(eventClass);
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
        this.eventsStats.values().forEach(EventsExecuteStatistics::reset);
        this.lastResetMs = System.currentTimeMillis();
    }

    @Getter
    public static class EventsExecuteStatistics {
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

        public EventsExecuteStatistics(@NotNull final MeterRegistry meterRegistry, @NotNull final Class<? extends Event> eventClass) {
            super();
            this.iterationCounter = meterRegistry.counter(METRIC_EVENT_COUNTER, METRIC_TAG_NAME_EVENT_CLASS, eventClass.getSimpleName());
            this.iterationTimer = meterRegistry.timer(METRIC_EVENT_TIMER, METRIC_TAG_NAME_EVENT_CLASS, eventClass.getSimpleName());
            reset();
        }

        /**
         * Фиксируется факт обработки события.
         *
         * @param timeMs Время, затраченное на обработку события.
         */
        public void setEventExecuted(long timeMs) {
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

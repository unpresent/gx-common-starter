package ru.gx.events;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.gx.utils.StringUtils;
import ru.gx.worker.StatisticsInfo;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class StandardEventsExecutorStatisticsInfo implements StatisticsInfo {
    private long lastResetMs;

    @Getter
    private int iterations;

    private long eventStartedMs;

    @Getter
    private final Map<Class<? extends Event>, EventsExecuteStatistics> eventsStats = new HashMap<>();

    public StandardEventsExecutorStatisticsInfo incIterations() {
        this.iterations++;
        return this;
    }

    public void eventExecuteStarting() {
        this.eventStartedMs = System.currentTimeMillis();
    }

    public void eventExecuteFinished(@NotNull final Event event) {
        var curEventStat = this.eventsStats.get(event.getClass());
        if (curEventStat == null) {
            curEventStat = new EventsExecuteStatistics();
            this.eventsStats.put(event.getClass(), curEventStat);
        }
        curEventStat.setEventExecuted(System.currentTimeMillis() - this.eventStartedMs);
    }

    @Override
    public String getInfoForLog() {
        final var str = new StringBuilder();
        str.append("stat by ");
        str.append(System.currentTimeMillis() - this.lastResetMs);
        str.append(" ms is {");
        this.eventsStats.keySet()
                .forEach(
                        eventClass -> {
                            final var eStat = this.eventsStats.get(eventClass);
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
                            str.append("; ");
                        }
                );
        return str.toString();
    }

    @Override
    public void reset() {
        this.lastResetMs = System.currentTimeMillis();
        this.iterations = 0;
        this.eventsStats.clear();
    }

    @Override
    public long lastResetMsAgo() {
        return System.currentTimeMillis() - this.lastResetMs;
    }

    @Getter
    public static class EventsExecuteStatistics {
        private int count;
        private long totalTimeMs = 0;
        private long minTimeMsPerEvent = -1;
        private long maxTimeMsPerEvent = 0;

        public void setEventExecuted(long timeMs) {
            this.count++;
            this.totalTimeMs += timeMs;
            if (this.minTimeMsPerEvent < 0 || timeMs < this.minTimeMsPerEvent) {
                this.minTimeMsPerEvent = timeMs;
            }
            if (timeMs > this.maxTimeMsPerEvent) {
                this.maxTimeMsPerEvent = timeMs;
            }
        }
    }
}

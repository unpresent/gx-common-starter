package ru.gx.core.worker;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PROTECTED;

public abstract class AbstractWorkerStatisticsInfo implements StatisticsInfo {
    public static final String METRIC_ITERATION_TIME = "iteration-time";
    public static final String METRIC_ITERATION_COUNT = "iteration-count";

    public static final String METRIC_TAG_WORKER_NAME = "worker";

    @Getter(PROTECTED)
    @NotNull
    private final AbstractWorker worker;

    @Getter(PROTECTED)
    @NotNull
    private final MeterRegistry meterRegistry;

    /**
     * Накопитель времен исполнений для отчета через actuator.
     * Не сбрасывается при reset().
     */
    @Getter(PROTECTED)
    private final Timer iterationTimer;

    /**
     * Количество исполнений с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private int count;

    /**
     * Общее время затраченное на обработку событий с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long totalTimeMs;

    /**
     * Минимальное время на исполнение одного события с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long minTimeMsPerIteration;

    /**
     * Максимальное время на исполнение одного события с момента последнего сброса.
     */
    @Getter(PROTECTED)
    private long maxTimeMsPerIteration;

    /**
     * Признак того, что с момента последнего сброса не было зафиксировано ни одной обработки события.
     */
    @Getter(PROTECTED)
    private volatile boolean isEmpty;

    @Getter
    private volatile long lastResetMs;

    /**
     * Накопитель количества исполнений для отчета через actuator.
     * Не сбрасывается при reset().
     */
    @Getter(PROTECTED)
    private final Counter iterationCounter;

    protected AbstractWorkerStatisticsInfo(@NotNull final AbstractWorker worker, @NotNull final MeterRegistry meterRegistry) {
        super();
        this.worker = worker;
        this.meterRegistry = meterRegistry;
        this.iterationCounter = meterRegistry.counter(METRIC_ITERATION_COUNT, METRIC_TAG_WORKER_NAME, worker.getWorkerName());
        this.iterationTimer = meterRegistry.timer(METRIC_ITERATION_TIME, METRIC_TAG_WORKER_NAME, worker.getWorkerName());
        this.privateClear();
    }

    @Override
    public String getPrintableInfo() {
        if (this.isEmpty) {
            return "Stat for last "  +
                    lastResetMsAgo() +
                    " ms for the worker " +
                    this.worker.getWorkerName() +
                    " is empty";
        }
        return "Stat for last " +
                lastResetMsAgo() +
                " ms for the worker iterations " +
                this.worker.getWorkerName() +
                " is: count = " +
                this.count +
                ", totalMs = " +
                this.totalTimeMs +
                ", minTime = " +
                this.minTimeMsPerIteration +
                ", maxTime = " +
                this.maxTimeMsPerIteration +
                ", avgTime = " +
                (this.count > 0 ? this.totalTimeMs / this.count : "NaN");
    }

    @Override
    public void reset() {
        privateClear();
    }

    /**
     * Чтобы никто не переопределял. Вызывается в конструкторе!
     */
    private void privateClear() {
        this.isEmpty = true;
        this.count = 0;
        this.totalTimeMs = 0;
        this.minTimeMsPerIteration = -1;
        this.maxTimeMsPerIteration = 0;
        this.lastResetMs = System.currentTimeMillis();
    }

    @Override
    public long lastResetMsAgo() {
        return System.currentTimeMillis() - this.lastResetMs;
    }

    public void iterationExecuted(long startedMs) {
        final var durationMs = System.currentTimeMillis() - startedMs;
        this.count++;
        this.totalTimeMs += durationMs;
        if (this.minTimeMsPerIteration < 0 || durationMs < this.minTimeMsPerIteration) {
            this.minTimeMsPerIteration = durationMs;
        }
        if (durationMs > this.maxTimeMsPerIteration) {
            this.maxTimeMsPerIteration = durationMs;
        }
        this.isEmpty = false;
    }
}

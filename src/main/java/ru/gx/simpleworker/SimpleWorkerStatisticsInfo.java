package ru.gx.simpleworker;

import io.micrometer.core.instrument.MeterRegistry;
import org.jetbrains.annotations.NotNull;
import ru.gx.worker.AbstractWorker;
import ru.gx.worker.AbstractWorkerStatisticsInfo;

public class SimpleWorkerStatisticsInfo extends AbstractWorkerStatisticsInfo {
    public SimpleWorkerStatisticsInfo(@NotNull AbstractWorker worker, @NotNull MeterRegistry meterRegistry) {
        super(worker, meterRegistry);
    }
}

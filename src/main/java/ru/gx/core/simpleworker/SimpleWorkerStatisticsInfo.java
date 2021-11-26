package ru.gx.core.simpleworker;

import io.micrometer.core.instrument.MeterRegistry;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.worker.AbstractWorker;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;

public class SimpleWorkerStatisticsInfo extends AbstractWorkerStatisticsInfo {
    public SimpleWorkerStatisticsInfo(@NotNull AbstractWorker worker, @NotNull MeterRegistry meterRegistry) {
        super(worker, meterRegistry);
    }
}

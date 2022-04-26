package ru.gx.core.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MetricsInitializer {
    private final static String METRIC_SERVICE_SETTINGS = "service.settings";
    private final static String METRIC_TAG_SERVICE_NAME = "name";

    @NotNull
    private final String serviceName;

    @NotNull
    private final MeterRegistry meterRegistry;

    public MetricsInitializer(
            @NotNull final String serviceName,
            @NotNull final MeterRegistry meterRegistry) {
        this.serviceName = serviceName;
        this.meterRegistry = meterRegistry;
        final var m = Gauge.builder(METRIC_SERVICE_SETTINGS, () -> 0)
                .tags(List.of(Tag.of(METRIC_TAG_SERVICE_NAME, this.serviceName)))
                .register(this.meterRegistry);
    }
}

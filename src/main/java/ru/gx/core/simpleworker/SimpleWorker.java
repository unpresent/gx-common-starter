package ru.gx.core.simpleworker;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import ru.gx.core.worker.AbstractWorker;
import ru.gx.core.worker.AbstractWorkerStatisticsInfo;

@SuppressWarnings("unused")
public class SimpleWorker extends AbstractWorker {
    public static final String WORKER_DEFAULT_NAME = "simple-worker";

    public SimpleWorker(
            @NotNull final String name,
            @NotNull final SimpleWorkerSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final ApplicationEventPublisher eventPublisher
    ) {
        super(name, settingsContainer, meterRegistry, eventPublisher);
        this.iterationExecuteEvent = new SimpleWorkerOnIterationExecuteEvent(this);
        this.startingExecuteEvent = new SimpleWorkerOnStartingExecuteEvent(this);
        this.stoppingExecuteEvent = new SimpleWorkerOnStoppingExecuteEvent(this);
    }

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути должен содержать логику итераций
     */
    @Getter
    @NotNull
    private final SimpleWorkerOnIterationExecuteEvent iterationExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван перед запуском Исполнителя.
     */
    @Getter
    @NotNull
    private final SimpleWorkerOnStartingExecuteEvent startingExecuteEvent;

    /**
     * Объект-команда, который является spring-event-ом. Его обработчик по сути будет вызван после останова Исполнителя.
     */
    @Getter
    @NotNull
    private final SimpleWorkerOnStoppingExecuteEvent stoppingExecuteEvent;

    @Override
    public void runnerIsLifeSet() {
        super.runnerIsLifeSet();
    }

    @EventListener(DoStartSimpleWorkerEvent.class)
    public void doStartSimpleWorkerEvent(DoStartSimpleWorkerEvent __) {
        this.start();
    }

    @EventListener(DoStopSimpleWorkerEvent.class)
    public void doStopSimpleWorkerEvent(DoStopSimpleWorkerEvent __) {
        this.stop();
    }

    @Override
    protected AbstractWorkerStatisticsInfo createStatisticsInfo() {
        return new SimpleWorkerStatisticsInfo(this, this.getMeterRegistry());
    }
}

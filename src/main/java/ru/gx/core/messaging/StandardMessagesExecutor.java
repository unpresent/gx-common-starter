package ru.gx.core.messaging;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class StandardMessagesExecutor extends AbstractMessagesExecutor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    public static final String WORKER_DEFAULT_NAME = "standard-messages-executor";

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public StandardMessagesExecutor(
            @NotNull final String name,
            @NotNull final StandardMessagesExecutorSettingsContainer settingsContainer,
            @NotNull final MeterRegistry meterRegistry,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final MessagesPrioritizedQueue messagesQueue
    ) {
        super(name, settingsContainer, meterRegistry, eventPublisher, messagesQueue);
    }

    @Override
    protected StandardMessagesExecutorStatisticsInfo createStatisticsInfo() {
        return new StandardMessagesExecutorStatisticsInfo(this, this.getMeterRegistry());
    }
    // -----------------------------------------------------------------------------------------------------------------
}

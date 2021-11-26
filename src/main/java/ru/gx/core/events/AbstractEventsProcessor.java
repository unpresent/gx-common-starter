package ru.gx.core.events;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Исполнитель событий.
 */
@Slf4j
public abstract class AbstractEventsProcessor implements EventsProcessor {

    @NotNull
    private final ApplicationEventPublisher eventPublisher;

    @NotNull
    private final StandardEventsExecutorStatisticsInfo eventsStatisticsInfo;

    protected AbstractEventsProcessor(@NotNull final ApplicationEventPublisher eventPublisher, @NotNull final StandardEventsExecutorStatisticsInfo eventsStatisticsInfo) {
        super();
        this.eventPublisher = eventPublisher;
        this.eventsStatisticsInfo = eventsStatisticsInfo;
    }

    /**
     * Извлечь событие из контейнера очередей и обработать его (вызвать обработчик).
     * @param queue Контейнер очередей.
     * @return True - событие было извлечено и обработано. False - нет событий в очереди.
     */
    @Override
    public Event pollEvent(@NotNull EventsPrioritizedQueue queue) {
        final var event = queue.pollEvent();
        if (event == null) {
            log.debug("No events in queue {}", queue.getName());
        } else {
            log.debug("Polled event " + event.getClass().getName());
        }
        return event;
    }

    @Override
    @NotNull
    public AbstractEventsProcessor processEvent(@NotNull Event event) {
        this.eventsStatisticsInfo.eventExecuteStarting();
        try {
            this.eventPublisher.publishEvent(event);
        } finally {
            this.eventsStatisticsInfo.eventExecuteFinished(event);
        }
        return this;
    }
}

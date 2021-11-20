package ru.gx.events;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static lombok.AccessLevel.PROTECTED;

/**
 * Исполнитель событий.
 */
@Slf4j
public abstract class AbstractEventsProcessor implements EventsProcessor {

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private ApplicationEventPublisher eventPublisher;

    protected AbstractEventsProcessor() {
        super();
    }

    /**
     * Извлечь событие из контейнера очередей и обработать его (вызвать обработчик).
     * @param queue Контейнер очередей.
     * @return True - событие было извлечено и обработано. False - нет событий в очереди.
     */
    @Override
    public Event pollAndProcessEvent(@NotNull EventsPrioritizedQueue queue) {
        final var event = queue.pollEvent();
        if (event == null) {
            log.debug("No events in queue {}", queue.getName());
        } else {
            log.debug("Polled event " + event.getClass().getName());
            this.eventPublisher.publishEvent(event);
        }
        return event;
    }
}

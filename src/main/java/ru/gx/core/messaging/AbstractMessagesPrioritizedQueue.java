package ru.gx.core.messaging;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Контейнер приоритезированных очередей.
 */
@Accessors(chain = true)
public abstract class AbstractMessagesPrioritizedQueue implements MessagesPrioritizedQueue {

    /**
     * Объект синхронизации.
     */
    private final Object monitor = new Object();

    /**
     * Имя компонента. Используется при логировании.
     */
    @Getter
    @NotNull
    private final String name;

    /**
     * Количество сообщения во всех очередях.
     */
    @NotNull
    private final AtomicInteger size = new AtomicInteger(0);

    /**
     * Очереди для каждого из приоритетов.
     */
    @NotNull
    private final List<Queue<Message<?>>> priorities = new ArrayList<>();

    /**
     * Максимальное количество сообщений, которое допускается в очереди.
     * Делаю volatile, чтобы была возможность изменить во время работы приложения.
     */
    @Getter
    private volatile int queueSizeLimit;

    /**
     * Конструктор контейнера очередей.
     *
     * @param name Имя контейнера. Используется для логирования.
     */
    protected AbstractMessagesPrioritizedQueue(@NotNull String name) {
        this.name = name;
    }

    /**
     * Инициализации компонента: создаются очереди по количеству приоритетов.
     *
     * @param maxQueueSize    Максимальное количество сообщений в очередях.
     * @param prioritiesCount Количество приоритетов.
     * @return this.
     */
    public AbstractMessagesPrioritizedQueue init(final int maxQueueSize, final int prioritiesCount) {
        synchronized (this.monitor) {
            while (this.priorities.size() < prioritiesCount) {
                this.priorities.add(new ArrayDeque<>());
            }
            for (var i = this.priorities.size() - 1; i >= prioritiesCount; i--) {
                if (this.priorities.get(i).size() == 0) {
                    this.priorities.remove(i);
                }
            }
            this.queueSizeLimit = maxQueueSize;
        }
        return this;
    }

    /**
     * Проверка на возможность бросить событие в очередь.
     *
     * @return true - контейнер очередей готов принять событие.
     */
    @Override
    public boolean allowPush() {
        // Не делаю synchronized, т.к. изменение queueSizeLimit может происходить крайне редко
        return size.get() < this.queueSizeLimit;
    }

    /**
     * Отправка события в контейнер очередей.
     *
     * @param priority Приоритет события.
     * @param message  Событие.
     * @return this.
     */
    @Override
    public AbstractMessagesPrioritizedQueue pushMessage(final int priority, @NotNull Message<? extends MessageBody> message) {
        synchronized (this.monitor) {
            if (priority < 0) {
                throw new InvalidParameterException("Priority can't be less 0!");
            }
            if (priority > priorities.size()) {
                throw new InvalidParameterException("Priority can't be more count of priorities!");
            }
            final var queue = priorities.get(priority);
            queue.offer(message);
            this.size.incrementAndGet();
        }
        return this;
    }

    /**
     * Извлечение события из контейнера очередей. Будет предоставлено наиболее старое событие из очереди с наименьшим приоритетом.
     *
     * @return Событие, которое надо обработать.
     */
    @Override
    public Message<? extends MessageBody> pollMessage() {
        if (this.size.get() <= 0) {
            return null;
        }
        synchronized (monitor) {
            for (final var pQueue : this.priorities) {
                if (pQueue.size() > 0) {
                    final var result = pQueue.poll();
                    if (result != null) {
                        this.size.decrementAndGet();
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Извлечение списка событий из контейнера очередей. Будут предоставлены наиболее старые события из очереди с наименьшим приоритетом.
     * Далее в результирующую коллекцию будут добавляться события из очереди со следующим приоритетом.
     * И так до тех пор, пока в результирующей коллекции не наберется заданное количество событий на обработку или пока не закончатся события в очередях.
     *
     * @param maxCount Сколько извлечь событий. Может быть предоставлено меньше событий (если они закончились в очереди).
     * @return Коллекция событий, которые надо обработать.
     */
    @Override
    @NotNull
    public Collection<Message<?>> pollMessages(final int maxCount) {
        final var result = new ArrayList<Message<?>>();
        if (this.size.get() <= 0) {
            return result;
        }
        synchronized (monitor) {
            var pIndex = 0;
            while (result.size() <= maxCount && pIndex < this.priorities.size()) {
                final var event = this.priorities.get(pIndex).poll();
                if (event != null) {
                    result.add(event);
                    if (result.size() >= maxCount) {
                        break;
                    }
                }
            }
            this.size.addAndGet(-result.size());
        }
        return result;
    }

    /**
     * @return Количество событий в контейнере очередей.
     */
    @Override
    public int queueSize() {
        return this.size.get();
    }

    /**
     * @return Количество приоритетов - по сути, количество очередей.
     */
    public int priorityCount() {
        return this.priorities.size();
    }
}

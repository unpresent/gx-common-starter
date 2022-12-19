package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Интерфейс контейнера приоритезированных очередей.
 */
@SuppressWarnings("unused")
public interface MessagesPrioritizedQueue {

    /**
     * Проверка на возможность бросить событие в очередь.
     *
     * @return true - контейнер очередей готов принять событие.
     */
    boolean allowPush();

    /**
     * Отправка системного (в очередь с приоритетом "0") события в контейнер очередей.
     *
     * @param message  Событие.
     */
    void pushSystemMessage(@NotNull final Object message);

    /**
     * Отправка события в контейнер очередей.
     *
     * @param priority Приоритет события.
     * @param message  Событие.
     */
    void pushMessage(final int priority, @NotNull final Object message);

    /**
     * Отправка события в контейнер очередей с предварительным ожиданием (если необходимо) доступности очереди.
     *
     * @param priority  Приоритет события.
     * @param message   Событие.
     * @param maxWaitMs Максимальное количество миллисекунд на ожидание. Если < 0, то допускается бесконечное ожидание.
     * @return Удалось ли принять сообщение в очередь.
     */
    boolean pushMessageWithWaits(
            final int priority,
            @NotNull final Object message,
            final long maxWaitMs
    ) throws InterruptedException;

    /**
     * Извлечение события из контейнера очередей. Будет предоставлено наиболее старое событие из очереди с наименьшим приоритетом.
     *
     * @return Событие, которое надо обработать.
     */
    @Nullable
    Object pollMessage();

    /**
     * Извлечение списка событий из контейнера очередей. Будут предоставлены наиболее старые события из очереди с наименьшим приоритетом.
     * Далее в результирующую коллекцию будут добавляться события из очереди со следующим приоритетом.
     * И так до тех пор, пока в результирующей коллекции не наберется заданное количество событий на обработку или пока не закончатся события в очередях.
     *
     * @param maxCount Сколько извлечь событий. Может быть предоставлено меньше событий (если они закончились в очереди).
     * @return Коллекция событий, которые надо обработать.
     */
    @NotNull
    List<Object> pollMessages(int maxCount);

    /**
     * Возврат сообщения в очередь, если при его обработке были ошибки, чтобы его можно было обработать повторно
     * @param priority Приоритет сообщения
     * @param message Сообщение
     */
    void returnErrorMessage(final int priority, @NotNull final Object message);

    /**
     * @return Количество событий в контейнере очередей.
     */
    int queueSize();

    /**
     * @return Количество приоритетов - по сути, количество очередей.
     */
    int priorityCount();

    /**
     * Настройка. Ограничение на количество событий в контейнере. При достижении данного ограничения новые сообщения
     * не будут приниматься контейнером, пока не будет освобожденно место путем извлечения событий.
     *
     * @return Допустимое количество событий в очереди.
     */
    int getQueueSizeLimit();

    /**
     * @return Название контейнера. Используется для вывода в логи.
     */
    String getName();
}

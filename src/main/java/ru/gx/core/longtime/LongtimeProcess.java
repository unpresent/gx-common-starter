package ru.gx.core.longtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Описатель длительно выполняемой задачи. Сценарий использования: создается в потоке REST-запроса.
 * Обрабатывается частями (действиями) в потоке MessageExecutor-а через события для каждой части (действия).
 */
@Data
@RequiredArgsConstructor
public class LongtimeProcess {

    /**
     * Идентификатор процесса
     */
    @NotNull
    private final UUID id;

    /**
     * Дата создания
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime created;

    /**
     * Дата старта
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime started;

    /**
     * Дата окончания
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime finished;

    /**
     * Текущий статус
     */
    private LongtimeProcessStatus status = LongtimeProcessStatus.WAITING;

    /**
     * Сколько всего планируется действий внутри процесса
     */
    private long total;

    /**
     * Сколько уже выполнено действий внутри процесса
     */
    private long current;

    /**
     * Пользователь, который запустил процесс
     */
    private String username;

    /**
     * Результат выполнения процесса. Для разных процессов может быть свой.
     */
    private Object result;

}

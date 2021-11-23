package ru.gx.worker;

import lombok.Getter;
import org.springframework.context.Lifecycle;

/**
 * Интерфейс исполнителей
 */
public interface Worker extends Lifecycle {
    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    OnIterationExecuteEvent getIterationExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при запуске Исполнителя.
     */
    OnStartingExecuteEvent getStartingExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при останове Исполнителя.
     */
    OnStoppingExecuteEvent getStoppingExecuteEvent();

    /**
     * @return Момент времени, когда Runner последний раз отчитывался, что работает.
     */
    long getLastRunnerLifeCheckedMs();

    /**
     * Метод, с помощью которого исполнитель отчитывается, что еще "жив".
     *
     * @see #getLastRunnerLifeCheckedMs()
     */
    void runnerIsLifeSet();

    /**
     * @return Получение статистики исполнения.
     */
    StatisticsInfo getStatisticsInfo();
}

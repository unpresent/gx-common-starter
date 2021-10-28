package ru.gx.worker;

import org.springframework.context.Lifecycle;

/**
 * Интерфейс исполнителей
 */
public interface Worker extends Lifecycle {
    /**
     * Настрйока (в мс), которая определяет сколько можно ждать штатного завершения исполнителя во время stop().
     */
    int getWaitOnStopMs();

    /**
     * Настрйока (в мс), которая определяет какую паузу надо выждать перед перезапуском после останова.
     */
    int getWaitOnRestartMs();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова итераций.
     */
    AbstractOnIterationExecuteEvent iterationExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при запуске Исполнителя.
     */
    AbstractOnStartingExecuteEvent startingExecuteEvent();

    /**
     * Требуется переопределить в наследнике.
     *
     * @return объект-событие, которое будет использоваться для вызова при останове Исполнителя.
     */
    AbstractOnStoppingExecuteEvent stoppingExecuteEvent();
}

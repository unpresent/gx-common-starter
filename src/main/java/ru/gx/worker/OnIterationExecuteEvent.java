package ru.gx.worker;

import ru.gx.events.Event;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление каждую итерацию цикла Worker-а.
 */
public interface OnIterationExecuteEvent extends Event {
    /**
     * Признак того, требуется ли перезапустить работу Исполнителя.
     * @see Worker
     */
    boolean isNeedRestart();

    /**
     * Признак того, требуется ли остановить работу Исполнителя.
     * @see Worker
     */
    boolean isStopExecution();

    /**
     * Требуется выполнить следующую итерацию, без пауз.
     * @see Worker
     * @see AbstractWorker
     */
    boolean isImmediateRunNextIteration();

    OnIterationExecuteEvent reset();
}

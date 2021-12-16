package ru.gx.core.worker;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление каждую итерацию цикла Worker-а.
 */
public interface OnIterationExecuteEvent {
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

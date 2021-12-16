package ru.gx.core.worker;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление перед запуском цикла Worker-а (при запуске и/или перезапуске).
 */
public interface OnStartingExecuteEvent {
    OnStartingExecuteEvent reset();
}

package ru.gx.core.worker;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление после окончания цикла Worker-а (при останове, перед перезапуском).
 */
public interface OnStoppingExecuteEvent {
    OnStoppingExecuteEvent reset();
}

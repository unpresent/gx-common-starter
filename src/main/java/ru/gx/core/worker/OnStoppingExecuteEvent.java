package ru.gx.core.worker;

import ru.gx.core.events.Event;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление после окончания цикла Worker-а (при останове, перед перезапуском).
 */
public interface OnStoppingExecuteEvent extends Event {
    OnStoppingExecuteEvent reset();
}

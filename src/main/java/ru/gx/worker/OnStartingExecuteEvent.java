package ru.gx.worker;

import ru.gx.events.Event;

/**
 * Объект-событие.<br/>
 * Слушатель данного события получает управление перед запуском цикла Worker-а (при запуске и/или перезапуске).
 */
public interface OnStartingExecuteEvent extends Event {
    OnStartingExecuteEvent reset();
}

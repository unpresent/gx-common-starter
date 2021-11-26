package ru.gx.core.channels;

/**
 * Способ обработки события о получении данных
 */
public enum IncomeDataProcessType {
    /**
     * Непосредственно при получении данных (в потоке получателя).
     */
    Immediate,

    /**
     * Отправить в очередь событий
     */
    SendToEventsQueue
}

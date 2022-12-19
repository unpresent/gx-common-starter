package ru.gx.core.longtime;

/**
 * Перечисление для статуса длительного процесса
 */
public enum LongtimeProcessStatus {

    /**
     * В ожидании
     */
    WAITING,

    /**
     * В процессе
     */
    IN_PROCESS,

    /**
     * Ошибка при обработке. Завершен
     */
    ERROR,

    /**
     * Завершен
     */
    FINISHED

}

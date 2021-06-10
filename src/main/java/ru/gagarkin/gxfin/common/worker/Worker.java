package ru.gagarkin.gxfin.common.worker;

/**
 * Интерфейс исполнителей
 */
public interface Worker {
    /**
     * Запуск исполнителя
     */
    boolean start(boolean autoRestart);

    /**
     * Попытка мягкой остновки исполнителя с ожиданием завершения
     * @param timeoutMs - ждать не более миллисекунд
     * @return true - остнов удался
     */
    boolean tryStop(int timeoutMs);

    /**
     * Останов без ожидания
     */
    void terminate(int timeoutMs);

    /**
     * Получение состояния:
     * @return true - исполнителья работает
     */
    boolean isRunning();
}

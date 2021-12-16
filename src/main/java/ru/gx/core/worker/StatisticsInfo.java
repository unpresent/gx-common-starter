package ru.gx.core.worker;

/**
 * Интерфейс для объекта хранения временной статистики.
 */
public interface StatisticsInfo {
    /**
     * @return Информация о собранной статистике для вывода в лог.
     */
    String getPrintableInfo();

    /**
     * Сброс статистических параметров.
     */
    void reset();

    /**
     * @return Сколько прошло миллисекунд с момента последнего сброса.
     */
    long lastResetMsAgo();
}

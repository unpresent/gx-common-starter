package ru.gx.core.worker;

/**
 * Интерфейс для объекта хранения временной статистики.
 */
public interface StatisticsInfo {
    /**
     * Общее количество исполнения итераций. Растёт постоянно.
     */
    String METRIC_EXECUTIONS_COUNT = "execs.count";

    /**
     * Суммарное время полезной работы.
     */
    String METRIC_EXECUTIONS_TIME = "execs.time";

    /**
     * Показатель. Процент полезной работы.
     */
    String METRIC_EXECUTIONS_BUSY_PERCENTS = "execs.busy-percents";

    /**
     * Через данную метрику будет передано имя Worker-а
     */
    String METRIC_WORKER_NAME = "worker-name";

    /**
     * Ярлык worker
     */
    String METRIC_TAG_WORKER_NAME = "worker";

    /**
     * Ярлык channel
     */
    String METRIC_TAG_CHANNEL_NAME = "channel";

    /**
     * @return Информация о собранной статистике для вывода в лог.
     */
    String getPrintableInfo();

    /**
     * Актуализация метрик и сброс статистических параметров.
     */
    void reset();

    /**
     * @return Сколько прошло миллисекунд с момента последнего сброса.
     */
    long lastResetMsAgo();
}

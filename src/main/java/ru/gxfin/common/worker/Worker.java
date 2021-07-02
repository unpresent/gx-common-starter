package ru.gxfin.common.worker;

import org.springframework.context.Lifecycle;

/**
 * Интерфейс исполнителей
 */
public interface Worker extends Lifecycle {
    /**
     * Настрйока (в мс), которая определяет сколько можно ждать штатного завершения исполнителя во время stop().
     */
    int getWaitOnStopMS();
}

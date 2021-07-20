package ru.gxfin.common.data;

/**
 * Интерфейс объектов, которые могут управляться пулом объектов.
 */
public interface PoolableObject {
    /**
     * Процедура очистки объекта.
     */
    void cleanOnReleaseToPool();
}

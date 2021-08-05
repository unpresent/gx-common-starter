package ru.gxfin.common.data;

/**
 * Интерфейс объектов, которые могут управляться пулом объектов.
 */
@Deprecated
public interface PoolableObject {
    /**
     * Процедура очистки объекта.
     */
    void cleanOnReleaseToPool();
}

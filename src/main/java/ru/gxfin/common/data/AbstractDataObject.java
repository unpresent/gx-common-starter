package ru.gxfin.common.data;

import java.io.Serializable;

/**
 * Базовый тип для DTO (объектов передачи данных).
 */
public abstract class AbstractDataObject implements DataObject, PoolableObject {
    /**
     * Процедура очистки объекта при возврате в пул.
     */
    @Override
    public void cleanOnReturnToPool() {
    }
}

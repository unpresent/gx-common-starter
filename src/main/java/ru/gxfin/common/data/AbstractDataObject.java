package ru.gxfin.common.data;

/**
 * Базовый тип для DTO (объектов передачи данных).
 */
public abstract class AbstractDataObject implements DataObject, PoolableObject {

    /**
     * Делаем конструктор protected, чтобы объекты создавали через фабрику, а не напрямую.
     */
    protected AbstractDataObject() {
        super();
    }

    /**
     * Процедура очистки объекта при возврате в пул.
     */
    @Override
    public void cleanOnReleaseToPool() {
    }
}

package ru.gxfin.common.data;

/**
 * Интерфейс объектов данных
 */
public interface DataObjectWithKey extends DataObject {
    /**
     * @return Идентифкатор объекта в рамках системы
     */
    @SuppressWarnings("unused")
    Object getKey();
}

package ru.gx.data;

/**
 * Базовый тип для DTO (объектов передачи данных).
 */
public abstract class AbstractDataObject implements DataObject {

    /**
     * Делаем конструктор protected, чтобы объекты создавали через фабрику, а не напрямую.
     */
    protected AbstractDataObject() {
        super();
    }
}

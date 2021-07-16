package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

/**
 * Базовый тип для DTO (объектов передачи данных), у которых есть ключ - локальный идентификатор системе.
 */
@ToString
public abstract class AbstractDataObjectWithKey extends AbstractDataObject implements DataObjectWithKey {
    protected AbstractDataObjectWithKey() {
        super();
    }

    /**
     * @return Ключ объекта - то, что его уникально иидентифицирует в рамках системы.
     */
    @JsonIgnore
    public abstract Object getKey();
}

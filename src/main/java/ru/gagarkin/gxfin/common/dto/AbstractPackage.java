package ru.gagarkin.gxfin.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Пакет DTO.
 * @param <T> Тип DT-объектов.
 */
public abstract class AbstractPackage<T extends AbstractDtoObject> {
    @JsonProperty(value = "package_size")
    public int packageSize;

    /**
     * Записи данного пакета
     */
    @JsonProperty(value = "rows")
    public T[] rows;

}

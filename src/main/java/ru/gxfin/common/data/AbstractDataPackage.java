package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Пакет DTO.
 * @param <T> Тип DT-объектов.
 */
public abstract class AbstractDataPackage<T extends AbstractDataObject> implements DataPackage<T> {
    /**
     * Элменты пакета - внутреннее хранение
     */
    @Getter(AccessLevel.PROTECTED)
    @JsonIgnore
    private final List<T> listItems = new ArrayList<>();

    /**
     * @return элменты пакета.
     */
    @Override
    @JsonProperty(value = "objects")
    public Collection<T> getObjects() {
        return this.getListItems();
    }

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    @JsonIgnore
    public T get(int index) {
        return getListItems().get(index);
    }

    /**
     * @return количество элментов в пакете
     */
    @Override
    @JsonIgnore
    public int size() {
        return getListItems().size();
    }
}


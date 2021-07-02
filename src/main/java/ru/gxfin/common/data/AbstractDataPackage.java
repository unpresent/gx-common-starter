package ru.gxfin.common.data;

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
    private final List<T> listItems = new ArrayList<>();

    /**
     * @return элменты пакета.
     */
    @JsonProperty("items")
    @Override
    public Collection<T> getItems() {
        return this.getListItems();
    }

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    public T get(int index) {
        return getListItems().get(index);
    }

    /**
     * @return количество элментов в пакете
     */
    @Override
    public int size() {
        return getListItems().size();
    }
}

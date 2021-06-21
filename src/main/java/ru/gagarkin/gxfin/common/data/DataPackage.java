package ru.gagarkin.gxfin.common.data;

import java.util.Collection;

/**
 * Пакет DTO.
 * @param <T> тип элементов.
 */
public interface DataPackage<T extends DataObject> {
    /**
     * @return Элменты пакета
     */
    Collection<T> getItems();

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    T get(int index);

    /**
     * @return количество элментов в пакете
     */
    int size();
}

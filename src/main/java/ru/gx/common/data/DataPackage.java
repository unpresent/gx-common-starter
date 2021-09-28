package ru.gx.common.data;

import java.util.Collection;

/**
 * Пакет объектов данных (Data Objects).
 * @param <T> тип элементов.
 */
public interface DataPackage<T extends DataObject> {
    /**
     * @return Элменты пакета
     */
    @SuppressWarnings("unused")
    Collection<T> getObjects();

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    @SuppressWarnings("unused")
    T get(int index);

    /**
     * @return количество элментов в пакете
     */
    @SuppressWarnings("unused")
    int size();
}

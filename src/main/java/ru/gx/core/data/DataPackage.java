package ru.gx.core.data;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Пакет объектов данных (Data Objects).
 * @param <T> тип элементов.
 */
public interface DataPackage<T extends DataObject> {
    /**
     * @return Элементы пакета
     */
    @SuppressWarnings("unused")
    @NotNull
    Collection<T> getObjects();

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    @SuppressWarnings("unused")
    @NotNull
    T get(int index);

    /**
     * @return количество элементов в пакете
     */
    @SuppressWarnings("unused")
    int size();
}

package ru.gx.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Пакет DTO.
 * @param <T> Тип DT-объектов.
 */
public abstract class AbstractDataPackage<T extends AbstractDataObject> implements DataPackage<T> {
    /**
     * Элементы пакета - внутреннее хранение
     */
    @Getter(AccessLevel.PROTECTED)
    @JsonIgnore
    @NotNull
    private final List<T> listObjects = new ArrayList<>();

    /**
     * @return элементы пакета.
     */
    @Override
    @JsonProperty("objects")
    @NotNull
    public Collection<T> getObjects() {
        return this.getListObjects();
    }

    /**
     * Доступ к элементу пакета
     * @param index индекс элемента
     * @return элемент с индексом index
     */
    @JsonIgnore
    @NotNull
    public T get(final int index) {
        return getListObjects().get(index);
    }

    /**
     * @return количество элементов в пакете
     */
    @Override
    @JsonIgnore
    public int size() {
        return getListObjects().size();
    }
}

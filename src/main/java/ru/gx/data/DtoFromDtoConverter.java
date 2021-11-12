package ru.gx.data;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Предназначен для объявления классов, которые будут конвертировать из одной модели данных в другую. При этом,
 * скорее всего, в памяти потребуется производить поиск существования объекта, который требуется получить.
 * @param <DEST> Тип объектов-результата.
 * @param <SRC> Тип исходных объектов, которые конвертируем в данные объектов-результата.
 */
@SuppressWarnings("unused")
public interface DtoFromDtoConverter<DEST extends DataObject, SRC extends DataObject> {

    /**
     * Поиск объекта одного типа по указанному источнику другого типа.
     * @param source        Объект, из которого берем данные.
     */
    DEST findDtoBySource(@NotNull final SRC source);

    /**
     * Создание объекта по источнику.
     * @param source        Объект, из которого берем данные.
     */
    DEST createDtoBySource(@NotNull final SRC source);

    /**
     * @param destination Объект-назначения данных.
     * @return Допустимо ли изменение объекта-назначения.
     */
    boolean isDestinationUpdatable(@NotNull final DEST destination);

    /**
     * Наполнение destination (DataObject) данными из source (DateObject).
     * @param destination   Объект, в который загружаем данные.
     * @param source        Объект, из которого берем данные.
     */
    void updateDtoBySource(@NotNull final DEST destination, @NotNull final SRC source) throws NotAllowedObjectUpdateException;

    /**
     * Наполнение пакета DTOs из списка объектов источника.
     * @param destination   Пакет DTOs.
     * @param source        Источник - список объектов-источников.
     */
    void fillDtoCollectionFromSource(@NotNull final Collection<DEST> destination, @NotNull final Iterable<SRC> source);
}

package ru.gx.data;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Базовый функционал классов, которые будут конвертировать из одной модели данных в другую. При этом,
 * скорее всего, в памяти потребуется производить поиск существования объекта, который требуется получить.
 * @param <DEST> Тип объектов-результата.
 * @param <SRC> Тип исходных объектов, которые конвертируем в данные объектов-результата.
 */
@SuppressWarnings("unused")
public abstract class AbstractDtoFromDtoConverter<DEST extends DataObject, SRC extends DataObject>
        implements DtoFromDtoConverter<DEST, SRC> {

    /**
     * Поиск объекта одного типа по указанному источнику другого типа.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    public abstract DEST findDtoBySource(@NotNull SRC source);

    /**
     * Создание объекта по источнику.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    public abstract DEST createDtoBySource(@NotNull SRC source);

    /**
     * @param destination Объект-назначения данных.
     * @return Допустимо ли изменение объекта-назначения.
     */
    @Override
    public abstract boolean isDestinationUpdatable(@NotNull DEST destination);

    /**
     * Наполнение destination (DataObject) данными из source (DateObject).
     * @param destination   Объект, в который загружаем данные.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    public abstract void updateDtoBySource(@NotNull DEST destination, @NotNull SRC source);

    /**
     * Наполнение пакета DTOs из списка объектов источника.
     * @param destination   Пакет DTOs.
     * @param source        Источник - список объектов-источников.
     */
    @Override
    public void fillDtoCollectionFromSource(@NotNull Collection<DEST> destination, @NotNull Iterable<SRC> source) {
        for (var sourceObject : source) {
            var destObject = findDtoBySource(sourceObject);
            if (destObject == null) {
                destObject = createDtoBySource(sourceObject);
            } else if (isDestinationUpdatable(destObject)) {
                updateDtoBySource(destObject, sourceObject);
            }
            destination.add(destObject);
        }
    }
}

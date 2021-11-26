package ru.gx.core.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Базовый функционал классов, которые будут конвертировать из одной модели данных в другую. При этом,
 * скорее всего, в памяти потребуется производить поиск существования объекта, который требуется получить.
 * @param <DEST> Тип объектов-результата.
 * @param <SRC> Тип исходных объектов, которые конвертируем в данные объектов-результата.
 */
@SuppressWarnings("unused")
public abstract class AbstractDtoFromDtoConvertor<DEST extends DataObject, SRC extends DataObject>
        implements DtoFromDtoConvertor<DEST, SRC> {

    /**
     * Поиск объекта одного типа по указанному источнику другого типа.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    @Nullable
    public abstract DEST findDtoBySource(@Nullable SRC source);

    /**
     * Создание объекта по источнику.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    @NotNull
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
    public abstract void updateDtoBySource(@NotNull DEST destination, @NotNull SRC source) throws NotAllowedObjectUpdateException;

    /**
     * Наполнение пакета DTOs из списка объектов источника.
     * @param destination   Пакет DTOs.
     * @param source        Источник - список объектов-источников.
     */
    @Override
    public void fillDtoCollectionFromSource(@NotNull Collection<DEST> destination, @NotNull Iterable<SRC> source) throws NotAllowedObjectUpdateException {
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

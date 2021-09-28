package ru.gx.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface DtoFromDtoConverter<DEST extends DataObject, DESTPACKAGE extends DataPackage<DEST>, SRC extends DataObject> {

    /**
     * Наполнение destination (DataObject) данными из source (DateObject).
     * @param destination   Объект, в который загружаем данные.
     * @param source        Объект, из которого берем данные.
     */
    void fillDtoFromDto(@NotNull final DEST destination, @NotNull final SRC source);

    /**
     * Наполнение пакета DTOs из списка объектов источника.
     * @param destination   Пакет DTOs.
     * @param source        Источник - список объектов-источников.
     */
    void fillDtoPackageFromDtoList(@NotNull final DESTPACKAGE destination, @NotNull final Iterable<SRC> source);
}

package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс для представления метаданных, которые сопровождают полезные данные.
 */
public interface MetadataGetter {
    /**
     * @return Количество значений (записей) в метаданных.
     */
    int metadataSize();

    /**
     * @param metadataKey Ключ записи в метаданных.
     * @return true - есть запись в метаданных с таким ключом, false - записи в метаданных с заданным ключом нет.
     */
    boolean containsMetadataKey(@NotNull final Object metadataKey);

    /**
     * Получение значения из метаданных по ключу.
     * @param metadataKey Ключ записи метаданных.
     * @return Значение записи метаданных для заданного ключа.
     */
    @Nullable
    Object getMetadataValue(@NotNull final Object metadataKey);

    /**
     * Получение списка всех записей метаданных (пары Ключ+Значение)
     * @return Список всех записей метаданных (пары Ключ+Значение)
     */
    Iterable<Metadata> getAllMetadata();
}

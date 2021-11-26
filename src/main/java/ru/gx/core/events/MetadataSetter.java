package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Интерфейс для записи метаданных, которые сопровождают полезные данные.
 */
@SuppressWarnings("UnusedReturnValue")
public interface MetadataSetter {

    /**
     * Записать в событие запись метаданных.
     * @param key Ключ записи метаданных.
     * @param value Значение записи метаданных.
     * @return this.
     */
    MetadataSetter putMetadata(@NotNull final Object key, @Nullable final Object value);

    /**
     * Перезаписать все метаданные.
     * @param source Новый список всех метаданных.
     * @return this.
     */
    MetadataSetter setMetadata(@Nullable final Iterable<Metadata> source);
}

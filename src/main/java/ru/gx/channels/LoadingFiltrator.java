package ru.gx.channels;

import org.jetbrains.annotations.NotNull;
import ru.gx.events.MetadataGetter;

/**
 * С помощью реализации данного интерфейса можно определять фильтрацию по загрузке данных.
 * На фильтрацию отдаются метаданные, которые сопровождают основные данные.
 * Если данные требуется обрабатывать, то метод {@link #allowProcess} должен вернуть true.
 */
public interface LoadingFiltrator {
    /**
     * @param metadata Метаданные, которые сопровождают бизнес-данные.
     *                 На их основании будет принято решение о необходимости обработки основных данных.
     * @return true - обрабатывать данные, false - проигнорировать данные.
     */
    boolean allowProcess(@NotNull final MetadataGetter metadata);
}

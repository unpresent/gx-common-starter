package ru.gx.core.data;

import org.jetbrains.annotations.NotNull;

public interface DataObjectKeyExtractor<O extends DataObject> {
    Object extractKey(@NotNull final O dataObject);
}

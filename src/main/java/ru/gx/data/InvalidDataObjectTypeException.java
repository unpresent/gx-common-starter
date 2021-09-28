package ru.gx.data;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class InvalidDataObjectTypeException extends Exception {
    public InvalidDataObjectTypeException(
            @Nullable final String message,
            @NotNull final DataObject object,
            @NotNull final Class<? extends DataObject> needType
    ) {
        super(StringUtils.isNotEmpty(message)
                ? message
                : "Invalid object type."
        + " Object type is " + object.getClass().getSimpleName() + "; expected object type is " + needType.getSimpleName()
        + " Object: " + object);
    }
}

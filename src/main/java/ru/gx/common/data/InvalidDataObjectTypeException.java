package ru.gx.common.data;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class InvalidDataObjectTypeException extends Exception {
    public InvalidDataObjectTypeException(String message, @NotNull DataObject object, @NotNull Class<? extends DataObject> needType) {
        super(StringUtils.isNotEmpty(message)
                ? message
                : "Invalid object type."
        + " Object type is " + object.getClass().getSimpleName() + "; expected object type is " + needType.getSimpleName()
        + " Object: " + object);
    }
}

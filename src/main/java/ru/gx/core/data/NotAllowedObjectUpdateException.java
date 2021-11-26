package ru.gx.core.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class NotAllowedObjectUpdateException extends Exception {
    public NotAllowedObjectUpdateException(@NotNull final Class<?> destinationObjectClass, @Nullable final String additionalInfo) {
        super("It isn't allowed update object of class " + destinationObjectClass.getName()
                + (additionalInfo != null ? "; info = " + additionalInfo : ""));
    }
}

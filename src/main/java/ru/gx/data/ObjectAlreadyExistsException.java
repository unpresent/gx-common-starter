package ru.gx.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ObjectAlreadyExistsException extends Exception {
    public ObjectAlreadyExistsException(@NotNull final Object key, @NotNull final DataObject object) {
        super("Object with key " + key + " already registered");
    }
}

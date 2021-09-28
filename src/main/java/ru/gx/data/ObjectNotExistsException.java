package ru.gx.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ObjectNotExistsException extends Exception {
    public ObjectNotExistsException(@NotNull final Object key, @NotNull final DataObject object) {
        super("Object with key " + key + " not registered");
    }
}

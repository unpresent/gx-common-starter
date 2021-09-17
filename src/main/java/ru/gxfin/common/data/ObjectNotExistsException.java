package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ObjectNotExistsException extends Exception {
    public ObjectNotExistsException(@NotNull Object key, @NotNull DataObject object) {
        super("Object with key " + key + " not registered");
    }
}

package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ObjectAlreadyExistsException extends Exception {
    public ObjectAlreadyExistsException(@NotNull Object key, @NotNull DataObject object) {
        super("Object with key " + key + " already registered");
    }
}

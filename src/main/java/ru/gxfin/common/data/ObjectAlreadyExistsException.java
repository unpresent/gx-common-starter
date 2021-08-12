package ru.gxfin.common.data;

@SuppressWarnings("unused")
public class ObjectAlreadyExistsException extends Exception {
    public ObjectAlreadyExistsException(Object key, DataObject object) {
        super("Object with key " + key + " already registered");
    }
}

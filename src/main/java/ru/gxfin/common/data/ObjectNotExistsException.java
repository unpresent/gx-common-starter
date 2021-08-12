package ru.gxfin.common.data;

@SuppressWarnings("unused")
public class ObjectNotExistsException extends Exception {
    public ObjectNotExistsException(Object key, DataObject object) {
        super("Object with key " + key + " not registered");
    }
}

package ru.gxfin.common.data;

public class ObjectsPoolException extends Exception {
    @SuppressWarnings("unused")
    public ObjectsPoolException(String message) {
        super(message);
    }

    public ObjectsPoolException(String message, Throwable cause) {
        super(message, cause);
    }
}

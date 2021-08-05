package ru.gxfin.common.data;

public class ObjectCreateException extends Exception {
    @SuppressWarnings("unused")
    public ObjectCreateException(String message) {
        super(message);
    }

    public ObjectCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}

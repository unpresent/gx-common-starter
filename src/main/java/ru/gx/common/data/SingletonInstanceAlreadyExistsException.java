package ru.gx.common.data;

public class SingletonInstanceAlreadyExistsException extends Exception {
    public SingletonInstanceAlreadyExistsException(String message) {
        super(message);
    }
}

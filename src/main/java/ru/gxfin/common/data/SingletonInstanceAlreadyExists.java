package ru.gxfin.common.data;

public class SingletonInstanceAlreadyExists extends Exception {
    public SingletonInstanceAlreadyExists(String message) {
        super(message);
    }
}

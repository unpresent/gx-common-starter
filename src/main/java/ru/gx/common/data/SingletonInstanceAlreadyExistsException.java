package ru.gx.common.data;

import org.jetbrains.annotations.NotNull;

public class SingletonInstanceAlreadyExistsException extends Exception {
    public SingletonInstanceAlreadyExistsException(@NotNull final String message) {
        super(message);
    }
}

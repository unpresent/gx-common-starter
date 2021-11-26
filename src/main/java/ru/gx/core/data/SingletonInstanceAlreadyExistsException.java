package ru.gx.core.data;

import org.jetbrains.annotations.NotNull;

public class SingletonInstanceAlreadyExistsException extends Exception {
    public SingletonInstanceAlreadyExistsException(@NotNull final String message) {
        super(message);
    }
}

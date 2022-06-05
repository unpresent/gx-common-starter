package ru.gx.core.messaging;

import org.jetbrains.annotations.NotNull;

public interface MessageExecutingBox<AB extends MessageBody, AM extends Message<AB>> {
    void setAnswer(@NotNull final AM answer);
}

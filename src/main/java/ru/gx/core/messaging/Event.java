package ru.gx.core.messaging;

public interface Event<B extends MessageBody>
        extends Message<EventHeader, B> {
}

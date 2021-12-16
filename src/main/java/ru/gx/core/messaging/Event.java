package ru.gx.core.messaging;

public interface Event<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
}

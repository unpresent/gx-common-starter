package ru.gx.core.messaging;

public interface DataPublish<H extends MessageHeader, B extends MessageBody>
        extends Message<H, B> {
}

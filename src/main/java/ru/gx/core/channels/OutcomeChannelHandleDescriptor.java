package ru.gx.core.channels;

import ru.gx.core.messaging.*;

/**
 * Интерфейс описателя канала отправления исходящих данных.
 */
@SuppressWarnings("unused")
public interface OutcomeChannelHandleDescriptor<M extends Message<? extends MessageHeader, ? extends MessageBody>>
        extends ChannelHandleDescriptor<M>, MetadataGetter, MetadataSetter {
}

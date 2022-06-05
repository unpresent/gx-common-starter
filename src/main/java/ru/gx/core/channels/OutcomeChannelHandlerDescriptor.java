package ru.gx.core.channels;

import ru.gx.core.messaging.*;

/**
 * Интерфейс описателя канала отправления исходящих данных.
 */
@SuppressWarnings("unused")
public interface OutcomeChannelHandlerDescriptor
        extends ChannelHandlerDescriptor, MetadataGetter, MetadataSetter {
}

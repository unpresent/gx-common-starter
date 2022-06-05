package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;

public interface InternalDescriptorsRegistrator {
    void internalRegisterDescriptor(@NotNull final ChannelHandlerDescriptor descriptor);
    void internalUnregisterDescriptor(@NotNull final ChannelHandlerDescriptor descriptor);
}

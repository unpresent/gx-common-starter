package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;

public interface InternalDescriptorsRegistrator {
    void internalRegisterDescriptor(@NotNull final ChannelHandleDescriptor<?> descriptor);
    void internalUnregisterDescriptor(@NotNull final ChannelHandleDescriptor<?> descriptor);
}

package ru.gx.core.channels;

import org.jetbrains.annotations.NotNull;

public interface InternalDescriptorsRegistrator {
    void internalRegisterDescriptor(@NotNull final ChannelDescriptor descriptor);
    void internalUnregisterDescriptor(@NotNull final ChannelDescriptor descriptor);
}

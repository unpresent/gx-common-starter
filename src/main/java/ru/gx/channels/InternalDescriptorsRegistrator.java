package ru.gx.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InternalDescriptorsRegistrator {
    void internalRegisterDescriptor(@NotNull final ChannelDescriptor descriptor);
    void internalUnregisterDescriptor(@NotNull final ChannelDescriptor descriptor);
}

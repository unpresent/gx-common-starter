package ru.gx.core.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelDescriptor;

@SuppressWarnings("UnusedReturnValue")
public interface DataEvent extends Event, MetadataGetter, MetadataSetter {
    /**
     * @return Описатель канала, по которому получены данные.
     */
    @NotNull
    ChannelDescriptor getChannelDescriptor();

    /**
     * @return Полезные данные в данном событии.
     */
    @Nullable
    Object getData();

    /**
     * Привязывание полезных данных к событию.
     * @param data Полезные данные.
     * @return this.
     */
    DataEvent setData(@Nullable final Object data);
}

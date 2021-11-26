package ru.gx.core.events;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnusedReturnValue")
public interface DataEvent extends Event, MetadataGetter, MetadataSetter {
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

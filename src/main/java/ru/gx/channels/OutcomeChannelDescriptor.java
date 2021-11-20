package ru.gx.channels;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.events.Metadata;
import ru.gx.events.MetadataGetter;
import ru.gx.events.MetadataSetter;

/**
 * Интерфейс описателя канала отправления исходящих данных.
 */
@SuppressWarnings("unused")
public interface OutcomeChannelDescriptor<O extends DataObject, P extends DataPackage<O>>
        extends ChannelDescriptor, MetadataGetter, MetadataSetter {

    /**
     * @return Класс объектов основных данных, которые будут передаваться в канале.
     */
    @Nullable
    Class<O> getDataObjectClass();

    /**
     * Установка класса объектов основных данных, которые будут передаваться в канале.
     * @param dataObjectClass Класс объектов основных данных.
     * @return this.
     */
    @NotNull
    OutcomeChannelDescriptor<O, P> setDataObjectClass(@Nullable final Class<O> dataObjectClass);

    /**
     * @return Класс пакета объектов основных данных, которые будут передаваться в канале.
     */
    @Nullable
    Class<P> getDataPackageClass();

    /**
     * Установка класс пакета объектов основных данных, которые будут передаваться в канале.
     * @param dataPackageClass Класс пакета объектов основных данных.
     * @return this.
     */
    @NotNull
    OutcomeChannelDescriptor<O, P> setDataPackageClass(@Nullable final Class<P> dataPackageClass);
}

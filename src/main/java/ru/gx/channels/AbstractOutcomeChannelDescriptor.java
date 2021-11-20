package ru.gx.channels;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.events.Metadata;
import ru.gx.events.MetadataSetter;
import ru.gx.events.SimpleMetadataContainer;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractOutcomeChannelDescriptor<O extends DataObject, P extends DataPackage<O>>
        extends AbstractChannelDescriptor
        implements OutcomeChannelDescriptor<O, P> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    private final SimpleMetadataContainer metadataContainer;

    /**
     * Класс объектов основных данных, которые будут передаваться в канале.
     */
    @Getter
    @Nullable
    private Class<O> dataObjectClass;

    /**
     * Класс пакета объектов основных данных, которые будут передаваться в канале.
     */
    @Getter
    @Nullable
    private Class<P> dataPackageClass;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractOutcomeChannelDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final String name,
            @Nullable final AbstractChannelDescriptorsDefaults defaults) {
        super(owner, name, ChannelDirection.In, defaults);
        this.metadataContainer = new SimpleMetadataContainer();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Implements MetadataGetter & MetadataSetter">
    @Override
    public int metadataSize() {
        return this.metadataContainer.metadataSize();
    }

    @Override
    public boolean containsMetadataKey(@NotNull final Object metadataKey) {
        return this.metadataContainer.containsMetadataKey(metadataKey);
    }

    @Override
    public @Nullable Object getMetadataValue(@NotNull final Object metadataKey) {
        return this.metadataContainer.getMetadataValue(metadataKey);
    }

    @Override
    public Iterable<Metadata> getAllMetadata() {
        return this.metadataContainer.getAllMetadata();
    }

    @Override
    public MetadataSetter putMetadata(@NotNull final Object key, @Nullable final Object value) {
        return this.metadataContainer.putMetadata(key, value);
    }

    @Override
    public MetadataSetter setMetadata(@Nullable Iterable<Metadata> source) {
        return this.metadataContainer.setMetadata(source);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">
    /**
     * Установка класса объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataObjectClass Класс объектов основных данных.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractOutcomeChannelDescriptor<O, P> setDataObjectClass(@Nullable final Class<O> dataObjectClass) {
        checkChangeable("dataObjectClass");
        this.dataObjectClass = dataObjectClass;
        return this;
    }

    /**
     * Установка класс пакета объектов основных данных, которые будут передаваться в канале.
     *
     * @param dataPackageClass Класс пакета объектов основных данных.
     * @return this.
     */
    @Override
    @NotNull
    public AbstractOutcomeChannelDescriptor<O, P> setDataPackageClass(@Nullable final Class<P> dataPackageClass) {
        checkChangeable("dataPackageClass");
        this.dataPackageClass = dataPackageClass;
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

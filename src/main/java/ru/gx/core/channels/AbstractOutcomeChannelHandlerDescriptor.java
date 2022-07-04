package ru.gx.core.channels;

import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.Metadata;
import ru.gx.core.messaging.SimpleMetadataContainer;

/**
 * Интерфейс описателя канала получения и обработки входящих данных.
 */
@Accessors(chain = true)
@SuppressWarnings("unused")
public abstract class AbstractOutcomeChannelHandlerDescriptor
        extends AbstractChannelHandlerDescriptor
        implements OutcomeChannelHandlerDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    private final SimpleMetadataContainer metadataContainer;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractOutcomeChannelHandlerDescriptor(
            @NotNull final ChannelsConfiguration owner,
            @NotNull final ChannelApiDescriptor<? extends Message<? extends MessageBody>> api,
            @Nullable final OutcomeChannelDescriptorsDefaults defaults
    ) {
        super(owner, api, ChannelDirection.Out, defaults);
        this.metadataContainer = new SimpleMetadataContainer();
        internalInitDefaults(defaults);
    }

    protected AbstractOutcomeChannelHandlerDescriptor(
            @NotNull ChannelsConfiguration owner,
            @NotNull String channelName,
            @Nullable OutcomeChannelDescriptorsDefaults defaults
    ) {
        super(owner, channelName, ChannelDirection.Out, defaults);
        this.metadataContainer = new SimpleMetadataContainer();
        internalInitDefaults(defaults);
    }

    protected void internalInitDefaults(@Nullable final OutcomeChannelDescriptorsDefaults defaults) {
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
    public AbstractOutcomeChannelHandlerDescriptor putMetadata(@NotNull final Object key, @Nullable final Object value) {
        this.metadataContainer.putMetadata(key, value);
        return this;
    }

    @Override
    public AbstractOutcomeChannelHandlerDescriptor setMetadata(@Nullable Iterable<Metadata> source) {
        this.metadataContainer.setMetadata(source);
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

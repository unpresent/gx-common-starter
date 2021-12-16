package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelHandleDescriptor;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "header")
public abstract class AbstractMessage<H extends AbstractMessageHeader, B extends AbstractMessageBody>
        implements Message<H, B> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Getters of Message">

    /**
     * Заголовок сообщения
     */
    @Getter
    @NotNull
    private final H header;

    /**
     * Тело сообщения.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private B body;

    /**
     * Не используемые данные, которые надо проигнорировать.
     */
    @Getter
    @Nullable
    private final MessageCorrelation correlation;

    @JsonIgnore
    @Getter
    @Setter
    @NotNull
    private ChannelHandleDescriptor<? extends Message<H, B>> channelDescriptor;

    @NotNull
    private final SimpleMetadataContainer metadataContainer = new SimpleMetadataContainer();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    protected AbstractMessage(
            @NotNull final H header,
            @Nullable final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        this.header = header;
        this.body = body;
        this.correlation = correlation;
        checkMessageType();
    }

    protected void checkMessageType() {
        MessageTypesRegistrator.checkType(this.header.getType(), this.getClass());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Implements MetadataGetter, MetadataSetter">

    /**
     * @return Количество значений (записей) в метаданных.
     */
    @JsonIgnore
    @Override
    public int metadataSize() {
        return this.metadataContainer.metadataSize();
    }

    /**
     * @param metadataKey Ключ записи в метаданных.
     * @return true - есть запись в метаданных с таким ключом, false - записи в метаданных с заданным ключом нет.
     */
    @JsonIgnore
    @Override
    public boolean containsMetadataKey(@NotNull final Object metadataKey) {
        return this.metadataContainer.containsMetadataKey(metadataKey);
    }

    /**
     * Получение значения из метаданных по ключу.
     * @param metadataKey Ключ записи метаданных.
     * @return Значение записи метаданных для заданного ключа.
     */
    @JsonIgnore
    @Override
    public Object getMetadataValue(@NotNull final Object metadataKey) {
        return this.metadataContainer.getMetadataValue(metadataKey);
    }

    /**
     * Получение списка всех записей метаданных (пары Ключ+Значение)
     * @return Список всех записей метаданных (пары Ключ+Значение)
     */
    @JsonIgnore
    @Override
    public Iterable<Metadata> getAllMetadata() {
        return this.metadataContainer.getAllMetadata();
    }

    /**
     * Записать в событие запись метаданных.
     * @param key Ключ записи метаданных.
     * @param value Значение записи метаданных.
     * @return this.
     */
    @JsonIgnore
    @Override
    public AbstractMessage<H, B> putMetadata(@NotNull final Object key, @Nullable final Object value) {
        this.metadataContainer.putMetadata(key, value);
        return this;
    }

    /**
     * Перезаписать все метаданные.
     * @param source Новый список всех метаданных.
     * @return this.
     */
    @JsonIgnore
    @Override
    public AbstractMessage<H, B> setMetadata(@Nullable final Iterable<Metadata> source) {
        this.metadataContainer.setMetadata(source);
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

package ru.gx.core.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelHandlerDescriptor;

import java.util.Objects;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = "header")
@ToString
public abstract class AbstractMessage<B extends MessageBody>
        implements Message<B> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Getters of Message">

    /**
     * Заголовок сообщения
     */
    @Getter
    @NotNull
    private final MessageHeader header;

    /**
     * Тело сообщения.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @NotNull
    private B body;

    /**
     * Не используемые данные, которые надо проигнорировать.
     */
    @Getter
    @Nullable
    private final MessageCorrelation correlation;

    /**
     * Описатель канала обработки сообщений, через который обрабатывается сообщение.
     * При обработке сообщения можно понять, какой канал его отправил на обработку.
     */
    @JsonIgnore
    @Nullable
    private ChannelHandlerDescriptor channelDescriptor;

    @NotNull
    private final SimpleMetadataContainer metadataContainer = new SimpleMetadataContainer();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">

    /**
     * Конструктор сообщения.
     * @param header Заголовок сообщения.
     * @param body Тело сообщения.
     * @param correlation Необрабатываемые данные.
     */
    protected AbstractMessage(
            @NotNull final MessageHeader header,
            @NotNull final B body,
            @Nullable final MessageCorrelation correlation
    ) {
        this.header = header;
        this.body = body;
        this.correlation = correlation;
        checkMessageType();
    }

    /**
     * Проверка на корректность (соответствие проверяемой тройки в регистрации) Вида, Типа и Класса сообщения.
     */
    protected void checkMessageType() {
        MessageTypesRegistrator.checkType(this.getHeader().getKind(), this.getHeader().getType(), this.getHeader().getVersion(), this.getClass(), this.getBody().getClass());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Implements MetadataGetter, MetadataSetter">

    @JsonIgnore
    @Override
    @NotNull
    public ChannelHandlerDescriptor getChannelDescriptor() {
        return Objects.requireNonNull(this.channelDescriptor);
    }

    @JsonIgnore
    @Override
    @NotNull
    public AbstractMessage<B> setChannelDescriptor(
            @NotNull final ChannelHandlerDescriptor channelDescriptor
    ) {
        this.channelDescriptor = channelDescriptor;
        return this;
    }

    @Override
    public boolean handleReady() {
        return this.channelDescriptor != null;
    }

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
    public AbstractMessage<B> putMetadata(@NotNull final Object key, @Nullable final Object value) {
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
    public AbstractMessage<B> setMetadata(@Nullable final Iterable<Metadata> source) {
        this.metadataContainer.setMetadata(source);
        return this;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}

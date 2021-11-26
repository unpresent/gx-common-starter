package ru.gx.core.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEvent;

/**
 * События, требующие обработки.<br/>
 * Внимание! Не должны быть Singleton. Должны быть Prototype.
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class AbstractDataEvent extends ApplicationEvent implements DataEvent {
    @NotNull
    private final SimpleMetadataContainer metadataContainer;

    @Nullable
    @Getter
    @Setter
    private Object data;

    protected AbstractDataEvent(@NotNull final Object source) {
        super(source);
        this.metadataContainer = new SimpleMetadataContainer();
    }

    /**
     * @return Количество значений (записей) в метаданных.
     */
    @Override
    public int metadataSize() {
        return this.metadataContainer.metadataSize();
    }

    /**
     * @param metadataKey Ключ записи в метаданных.
     * @return true - есть запись в метаданных с таким ключом, false - записи в метаданных с заданным ключом нет.
     */
    @Override
    public boolean containsMetadataKey(@NotNull final Object metadataKey) {
        return this.metadataContainer.containsMetadataKey(metadataKey);
    }

    /**
     * Получение значения из метаданных по ключу.
     * @param metadataKey Ключ записи метаданных.
     * @return Значение записи метаданных для заданного ключа.
     */
    @Override
    public Object getMetadataValue(@NotNull final Object metadataKey) {
        return this.metadataContainer.getMetadataValue(metadataKey);
    }

    /**
     * Получение списка всех записей метаданных (пары Ключ+Значение)
     * @return Список всех записей метаданных (пары Ключ+Значение)
     */
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
    @Override
    public AbstractDataEvent putMetadata(@NotNull final Object key, @Nullable final Object value) {
        this.metadataContainer.putMetadata(key, value);
        return this;
    }

    /**
     * Перезаписать все метаданные.
     * @param source Новый список всех метаданных.
     * @return this.
     */
    @Override
    public AbstractDataEvent setMetadata(@Nullable final Iterable<Metadata> source) {
        this.metadataContainer.setMetadata(source);
        return this;
    }
}
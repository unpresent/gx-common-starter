package ru.gx.events;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SimpleMetadataContainer implements MetadataGetter, MetadataSetter {
    @NotNull
    private final Map<Object, Object> metadata = new HashMap<>();

    /**
     * @return Количество значений (записей) в метаданных.
     */
    @Override
    public int metadataSize() {
        return this.metadata.size();
    }

    /**
     * @param metadataKey Ключ записи в метаданных.
     * @return true - есть запись в метаданных с таким ключом, false - записи в метаданных с заданным ключом нет.
     */
    @Override
    public boolean containsMetadataKey(@NotNull final Object metadataKey) {
        return this.metadata.containsKey(metadataKey);
    }

    /**
     * Получение значения из метаданных по ключу.
     * @param metadataKey Ключ записи метаданных.
     * @return Значение записи метаданных для заданного ключа.
     */
    @Override
    public Object getMetadataValue(@NotNull final Object metadataKey) {
        return this.metadata.get(metadataKey);
    }

    /**
     * Получение списка всех записей метаданных (пары Ключ+Значение)
     * @return Список всех записей метаданных (пары Ключ+Значение)
     */
    @Override
    public Iterable<Metadata> getAllMetadata() {
        // TODO: Реализовать Iterator<EventMetadata> с обертками EventMetadata, которые не будут копировать в себя Key + Value.
        final var result = new ArrayList<Metadata>();
        this.metadata
                .keySet()
                .forEach(k -> result.add(new EventMetadataImpl(k, this.getMetadataValue(k))));
        return result;
    }

    /**
     * Записать в событие запись метаданных.
     * @param key Ключ записи метаданных.
     * @param value Значение записи метаданных.
     * @return this.
     */
    @Override
    public SimpleMetadataContainer putMetadata(@NotNull final Object key, @Nullable final Object value) {
        this.metadata.put(key, value);
        return this;
    }

    /**
     * Перезаписать все метаданные.
     * @param source Новый список всех метаданных.
     * @return this.
     */
    @Override
    public SimpleMetadataContainer setMetadata(@Nullable final Iterable<Metadata> source) {
        this.metadata.clear();
        if (source != null) {
            source.forEach(s -> putMetadata(s.getKey(), s.getValue()));
        }
        return this;
    }

    private static class EventMetadataImpl implements Metadata {
        @Getter
        private final Object key;

        @Getter
        private final Object value;

        private EventMetadataImpl(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}

package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMemRepo<E extends AbstractDataObject> implements ObjectIdResolver {
    private final Map<Object, E> objects = new HashMap<>();

    @Override
    public void bindItem(ObjectIdGenerator.IdKey id, Object pojo) {
        final var old = this.objects.get(id.key);
        if (old != null) {
            if (Objects.equals(old, pojo)) {
                return;
            }
            // TODO: Обновление объекта!
            throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
        }
        this.objects.put(id.key, (E)pojo);
    }

    @Override
    public E resolveId(ObjectIdGenerator.IdKey id) {
        return this.objects.get(id.key);
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        // TODO: Проверить для репо с наследованием. Например, AbstractInstrument и его наследники: Security и Currency.
        // Явно кто-то от кого-то должен наследоваться.
        return resolverType.getClass() == this.getClass();
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object context) {
        return this;
    }

}

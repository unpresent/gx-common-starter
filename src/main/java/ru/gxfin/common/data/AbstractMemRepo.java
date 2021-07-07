package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMemRepo<E extends AbstractDataObject, P extends DataPackage<E>>
        implements DataMemRepo<E> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    private final ObjectMapper objectMapper;

    private static AbstractIdResolver idResolver;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractMemRepo(ObjectMapper objectMapper, AbstractIdResolver idResolver) {
        this.objectMapper = objectMapper;
        this.idResolver = idResolver;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация DataMemRepo">
    public abstract Class<E> getObjectClass();

    public abstract Class<P> getPackageClass();

    @Override
    public E deserializeObject(String jsonObject) throws JsonProcessingException {
        final var objectClass = getObjectClass();
        if (objectClass != null) {
            return this.objectMapper.readValue(jsonObject, objectClass);
        } else {
            return null;
        }
    }

    @Override
    public DataPackage<E> deserializePackage(String jsonPackage) throws JsonProcessingException {
        final var packageClass = getPackageClass();
        if (packageClass != null) {
            return this.objectMapper.readValue(jsonPackage, packageClass);
        } else {
            return null;
        }
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    protected static abstract class AbstractIdResolver implements ObjectIdResolver {
        @Getter(AccessLevel.PROTECTED)
        private final Map<Object, Object> objects = new HashMap<>();

        public AbstractIdResolver() {
            super();
            idResolver = this;
        }
        // -------------------------------------------------------------------------------------------------------------
        // <editor-fold desc="реализация ObjectIdResolver">
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
            this.objects.put(id.key, pojo);
        }

        @Override
        public Object resolveId(ObjectIdGenerator.IdKey id) {
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
        // </editor-fold>
        // -------------------------------------------------------------------------------------------------------------
    }
}

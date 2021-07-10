package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractMemRepo<E extends AbstractDataObject, P extends DataPackage<E>>
        implements DataMemRepo<E>, ObjectsPool<E> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(AccessLevel.PROTECTED)
    private static volatile AbstractMemRepo instance;

    @Getter(AccessLevel.PROTECTED)
    private final ObjectMapper objectMapper;

    @Getter(AccessLevel.PROTECTED)
    private static AbstractIdResolver idResolver;

    private final ObjectsPool objectsPool;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractMemRepo(ObjectMapper objectMapper, AbstractIdResolver idResolver, boolean isConcurrent, int initSize) throws SingletonInstanceAlreadyExists, ObjectsPoolException {
        this.objectMapper = objectMapper;
        this.idResolver = idResolver;

        final var thisClass = this.getClass();
        synchronized (thisClass) {
            if (instance != null) {
                throw new SingletonInstanceAlreadyExists("Singleton instance already exists! Class = " + thisClass.getName());
            }
            instance = this;
        }

        if (isConcurrent) {
            this.objectsPool = new ConcurrentObjectsPool(true, initSize);
        } else {
            this.objectsPool = new SimpleObjectsPool(true, initSize);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ObjectPool">
    /**
     * Создание объекта репозитория. Можно переопределить в наследнике и там создавать нормально.
     * @return Объект репозитория.
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected E internalCreateObject() throws ObjectsPoolException {
        final var objectClass = getObjectClass();
        try {
            final var constructor = objectClass.getConstructor();
            final var result = constructor.newInstance();
            return result;
        } catch (Exception e) {
            throw new ObjectsPoolException(e.getMessage(), e);
        }
    }

    /**
     * Получение объекта из пула. Если в пуле нет, то создается новый.
     * @return Объект репозитория.
     */
    public E pollObject() throws ObjectsPoolException {
        return (E)this.objectsPool.pollObject();
    }

    /**
     * Возвращаем более неиспользуемый объект в пул.
     * @param object Объект репозитория.
     */
    public void returnObject(E object) {
        this.objectsPool.returnObject(object);
    }

    @Override
    public int freeObjectsCount() {
        return this.objectsPool.freeObjectsCount();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация DataMemRepo">
    /**
     * @return Класс объектов репозитория.
     */
    public abstract Class<E> getObjectClass();

    /**
     * @return Класс пакета объектов.
     */
    public abstract Class<P> getPackageClass();

    /**
     * Десериализация json-а в объект
     *
     * @param jsonObject json-строка с объектом
     * @return объект в виде DataObject
     */
    @Override
    public E deserializeObject(String jsonObject) throws JsonProcessingException {
        final var objectClass = getObjectClass();
        if (objectClass != null) {
            return this.objectMapper.readValue(jsonObject, objectClass);
        } else {
            return null;
        }
    }

    /**
     * Десериализация json-а в пакет объектов
     *
     * @param jsonPackage json-строка с пакетом объектов
     * @return пакет объектов в виде DataPackage
     */
    @Override
    public DataPackage<E> deserializePackage(String jsonPackage) throws JsonProcessingException {
        final var packageClass = getPackageClass();
        if (packageClass != null) {
            return this.objectMapper.readValue(jsonPackage, packageClass);
        } else {
            return null;
        }
    }

    /**
     * Получение объекта по иденификатору (ключу), который указан у класса в @JsonIdentityInfo.
     *
     * @param key значение ключа, по которому ищем объект.
     * @return объект, если такой найден; null, если по такому ключу в IdResolver-е нет объекта.
     */
    @Override
    public E getByKey(Object key) {
        return (E) idResolver.getObjects().get(key);
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

    protected static abstract class AbstractObjectsFactory {
        public static AbstractDataObject createInstance() throws ObjectsPoolException {
            return getInstance().internalCreateObject();
        }
    }

    protected static class SimpleObjectsPool extends AbstractSimpleObjectsPool<PoolableObject> {
        protected SimpleObjectsPool(boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
            super(allowCreateObjects, initSize);
        }

        @Override
        protected PoolableObject createObject() throws ObjectsPoolException {
            return getInstance().internalCreateObject();
        }
    }

    protected static class ConcurrentObjectsPool extends AbstractConcurrentObjectsPool<PoolableObject> {
        protected ConcurrentObjectsPool(boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
            super(allowCreateObjects, initSize);
        }

        @Override
        protected PoolableObject createObject() throws ObjectsPoolException {
            return getInstance().internalCreateObject();
        }
    }
}

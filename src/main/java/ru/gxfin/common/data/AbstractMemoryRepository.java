package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Базовая непотокобезопасная реализация InMemory-репозитория объектов типа E.
 * <br/>
 * В поле {@link #objects} находятся объекты, с которыми сейчас работают.
 * В {@link #objectsPool} находятся заготовки объектов - свободные, которые выделяются при запросе у пула объекта.}
 * <p/>
 * Если в наследнике сделать публичным {@link AbstractIdResolver}, это будет означать, что все объекты обслуживаемого типа
 * и его наследники имеют сквозную идентификацию. Например, Инструмент (базовый) и наследники Бумага, Валюта и Дериватив.
 * Если сквозная идентификация должна быть общая для всех Инструментов, то в Репозитории инструментов требуется определить
 * публичного наследника {@link AbstractIdResolver}. И его уже использовать при определении классов DTO Бумаги, Валюты
 * и Деривативов.
 * <p/>
 * Если в наследнике сделать публичным {@link AbstractObjectsFactory}, это будет означать, что объекты обслуживаемого типа
 * можно создавать. В классах элементов нужно перехватывать с помощью @JsonCreator момент создания
 * и передать управление фабрике. Например, Инструмент (базовый) и наследники Бумаги, Валюты и Деривативы.
 * Тогда в репозитории Инструментов будет неуместно определять Фабрику, а в репозиториях Бумаг, Валют и Дериватов
 * необходимо определить Фабрику, которая будет наследником от {@link AbstractObjectsFactory}. В классах Бумага, Валюта
 * и Дериватив необходимо перехватить создание объекта @JsonCreate-ом и вызвать
 * {@link AbstractObjectsFactory#getOrCreateObject}
 * <p/>
 * В наследниках рекомендуется переопределить {@link #internalCreateEmptyInstance()}, в которой создавать объект
 * оператором new() без вызова данной реализации через super.
 * <p/>
 * @param <E> Тип экземпляров, которыми управляет репозиторий.
 * @param <P> Тип пакетов объектов, которыми управляет репозиторий.
 */
public abstract class AbstractMemoryRepository<E extends AbstractDataObjectWithKey, P extends DataPackage<E>>
        implements DataMemoryRepository<E>, ObjectsPool<E> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PROTECTED)
    private static final Map<Class<? extends AbstractMemoryRepository>, AbstractMemoryRepository> instancesByRepositoryClass = new HashMap<>();

    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PROTECTED)
    private static final Map<Class<? extends AbstractDataObjectWithKey>, AbstractMemoryRepository> instancesByObjectsClass = new HashMap<>();

    @SuppressWarnings("rawtypes")
    protected static AbstractMemoryRepository getRepositoryByClass(Class<? extends AbstractMemoryRepository> repositoryClass) {
        return instancesByRepositoryClass.get(repositoryClass);
    }

    @SuppressWarnings("rawtypes")
    protected static AbstractMemoryRepository getRepositoryByObjectsClass(Class<? extends AbstractDataObjectWithKey> objectsClass) {
        return instancesByObjectsClass.get(objectsClass);
    }

    @Getter(AccessLevel.PROTECTED)
    private final ObjectMapper objectMapper;

    @SuppressWarnings("rawtypes")
    private final ObjectsPool objectsPool;

    @Getter(AccessLevel.PROTECTED)
    private final Map<Object, E> objects = new HashMap<>();

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    protected AbstractMemoryRepository(ObjectMapper objectMapper, boolean isConcurrent, int initSize) throws SingletonInstanceAlreadyExistsException, ObjectsPoolException {
        this.objectMapper = objectMapper;

        final var thisClass = this.getClass();
        final var objectsClass = this.getObjectClass();
        synchronized (thisClass) {
            var instance = getRepositoryByClass(thisClass);
            if (instance != null) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered! Class = " + thisClass.getName());
            }
            instancesByRepositoryClass.put(thisClass, this);

            instance = getRepositoryByObjectsClass(objectsClass);
            if (instance != null) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered (by Objects Class)! ObjectsClass = " + objectsClass.getName());
            }
            instancesByObjectsClass.put(objectsClass, this);
        }

        if (isConcurrent) {
            this.objectsPool = new ConcurrentObjectsPool(this, true, initSize);
        } else {
            this.objectsPool = new SimpleObjectsPool(this, true, initSize);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ObjectPool">
    /**
     * Создание "заготовки" объекта. Вызывается Пулом объектов.
     * Рекомендуется переопределить (без вызова данной реализации через super) в наследнике
     * и там создавать нормально через оператор new().
     * @return Объект репозитория.
     * @throws ObjectsPoolException Ошибки при создании экземпляра объекта.
     */
    protected E internalCreateEmptyInstance() throws ObjectsPoolException {
        final var objectClass = getObjectClass();
        try {
            final var constructor = objectClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new ObjectsPoolException(e.getMessage(), e);
        }
    }

    /**
     * Получение объекта из пула. Если в пуле нет, то создается новый.
     * @return Объект репозитория.
     */
    @SuppressWarnings("unchecked")
    public E pollObject() throws ObjectsPoolException {
        return (E) this.objectsPool.pollObject();
    }

    /**
     * Возвращаем более неиспользуемый объект в пул.
     * Удаляем из спска объектов в IdResolver-е.
     * @param object Объект репозитория.
     */
    @SuppressWarnings("unchecked")
    public void releaseObject(E object) {
        if (object != null) {
            this.objects.remove(object.getKey());
            this.objectsPool.releaseObject(object);
        }
    }

    /**
     * @return Количество свободных объектов.
     */
    @Override
    public int freeObjectsCount() {
        return this.objectsPool.freeObjectsCount();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация DataMemoryRepository">

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
    public E loadObject(String jsonObject) throws JsonProcessingException {
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
    public DataPackage<E> loadPackage(String jsonPackage) throws JsonProcessingException {
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
        return this.getObjects().get(key);
    }

    public boolean containsKey(Object key) {
        return this.objects.containsKey(key);
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация Iterable">
    @Override
    public Iterator<E> iterator() {
        return this.objects.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.objects.values().forEach(action);
    }

    @Override
    public Spliterator<E> spliterator() {
        return this.objects.values().spliterator();
    }

    @SuppressWarnings("unused")
    public int size() {
        return this.objects.size();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ObjectIdResolver">
    @SuppressWarnings("unchecked")
    protected void bindItem(ObjectIdGenerator.IdKey id, Object pojo) {
        final var old = AbstractMemoryRepository.this.objects.get(id.key);
        if (old != null) {
            if (Objects.equals(old, pojo)) {
                return;
            }
            // TODO: Обновление объекта!
            throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
        }
        AbstractMemoryRepository.this.objects.put(id.key, (E)pojo);
    }

    protected Object resolveId(ObjectIdGenerator.IdKey id) {
        return AbstractMemoryRepository.this.objects.get(id.key);
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация AbstractObjectsFactory">
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    /**
     * IdResolver нужен для определения объекта по его ключу - нужен для ObjectMapper-а jackson-а.
     * При десериализации объекта jackson также регистриует объект с ключом в IdResolver-е.
     * Это используется для наполнения списка объектов Репозитория {@link #objects}.
     */
    @SuppressWarnings("rawtypes")
    protected static abstract class AbstractIdResolver implements ObjectIdResolver {
        private AbstractMemoryRepository repository;

        @SneakyThrows
        public AbstractIdResolver() {
            super();
        }

        protected abstract Class<? extends AbstractMemoryRepository> getRepositoryClass();

        private AbstractMemoryRepository getRepository() {
            if (this.repository == null) {
                this.repository = getRepositoryByClass(getRepositoryClass());
            }
            return this.repository;
        }
        // -------------------------------------------------------------------------------------------------------------
        // <editor-fold desc="реализация ObjectIdResolver">
        @Override
        public void bindItem(ObjectIdGenerator.IdKey id, Object pojo) {
            getRepository().bindItem(id, pojo);
        }

        @Override
        public Object resolveId(ObjectIdGenerator.IdKey id) {
            return getRepository().resolveId(id);
        }

        @Override
        public boolean canUseFor(@NotNull ObjectIdResolver resolverType) {
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

    /**
     * Фабрика объектов. Предназначена для выделения экземпляров объектов
     * с предварительной проверкой на существование объекта с таким же ключом.
     */
    @SuppressWarnings("unused")
    protected static abstract class AbstractObjectsFactory {
        /**
         * Если объект с таким ключом уже зарегистрирован в репозитории, то будет выдан этот существующий объект.
         * Если объекта с таким ключом нет, от выдается из пула свободная "заготовка"
         * (если в пуле закончились "заготовки", создается новый экземпляр).
         * @param key Ключ, по которому ищется объект в Репозитории
         * @return Уже существующий и ранее зарегестрированный объект в Репозитории, или заготовка из Пула, или новый экземпляр.
         * @throws ObjectsPoolException Ошибка при выделении объекта из Пула
         * (например, "заготовки" закончились, а создавать новый экземпляр запрещено).
         */
        @SuppressWarnings("unchecked")
        protected static <X extends AbstractDataObjectWithKey> X getOrCreateObject(Class<X> objectClass, Object key) throws ObjectsPoolException {
            final var owner = getRepositoryByObjectsClass(objectClass);
            final var result = (X)owner.getByKey(key);
            if (result != null) {
                return result;
            }

            return (X) owner.objectsPool.pollObject();
        }
    }
    /**
     * Пул объектов предназначен для выдачи "заготовок" объектов по требованию {@link #pollObject()}.
     * Также в пул можно вернуть {@link #releaseObject} уже более неиспользуемый объект,
     * который в этом случае почистится и станет "заготовкой".
     */
    @SuppressWarnings("rawtypes")
    protected static class SimpleObjectsPool extends AbstractSimpleObjectsPool<PoolableObject> {
        protected SimpleObjectsPool(AbstractMemoryRepository owner, boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
            super(owner, allowCreateObjects, initSize);
        }

        protected AbstractMemoryRepository getOwnerRepository() {
            return (AbstractMemoryRepository)getOwner();
        }

        /**
         * Создание новой заготовки. Вызывается из {@link #objectsPool}
         * @return Объект-заготовка.
         * @throws ObjectsPoolException Ошибка при создании объекта-заготовки
         * (например, если запрещено создавать новые экземпляры)
         */
        @Override
        protected PoolableObject createInstance() throws ObjectsPoolException {
            return this.getOwnerRepository().internalCreateEmptyInstance();
        }
    }

    /**
     * Пул объектов предназначен для выдачи "заготовок" объектов по требованию {@link #pollObject()}.
     * Также в пул можно вернуть {@link #releaseObject} уже более неиспользуемый объект,
     * который в этом случае почистится и станет "заготовкой".
     */
    @SuppressWarnings("rawtypes")
    protected static class ConcurrentObjectsPool extends AbstractConcurrentObjectsPool<PoolableObject> {
        protected ConcurrentObjectsPool(AbstractMemoryRepository owner, boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
            super(owner, allowCreateObjects, initSize);
        }

        protected AbstractMemoryRepository getOwnerRepository() {
            return (AbstractMemoryRepository)getOwner();
        }

        /**
         * Создание новой заготовки. Вызывает из {@link #objectsPool}
         * @return Объект-заготовка.
         * @throws ObjectsPoolException Ошибка при создании объекта-заготовки
         * (например, если запрещено создавать новые экземпляры)
         */
        @Override
        protected PoolableObject createInstance() throws ObjectsPoolException {
            return this.getOwnerRepository().internalCreateEmptyInstance();
        }
    }
}


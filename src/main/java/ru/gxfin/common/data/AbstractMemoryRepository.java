package ru.gxfin.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Consumer;

/**
 * Базовая непотокобезопасная реализация InMemory-репозитория объектов типа E.
 * <br/>
 * В поле {@link #objects} находятся объекты, с которыми сейчас работают.
 * // В {objectsPool} находятся заготовки объектов - свободные, которые выделяются при запросе у пула объекта.}
 * <p/>
 * Если в наследнике сделать публичным {@link AbstractIdResolver}, это будет означать, что все объекты обслуживаемого типа
 * и его наследники имеют сквозную идентификацию. Например, Инструмент (базовый) и наследники Бумага, Валюта и Дериватив.
 * Если сквозная идентификация должна быть общая для всех Инструментов, то в Репозитории инструментов требуется определить
 * публичного наследника {@link AbstractIdResolver}. И его уже использовать при определении классов DTO Бумаги, Валюты
 * и Деривативов.
 * <p/>
 *
 * @param <O> Тип экземпляров, которыми управляет репозиторий.
 * @param <P> Тип пакетов объектов, которыми управляет репозиторий.
 */
public abstract class AbstractMemoryRepository<O extends AbstractDataObject, P extends DataPackage<O>>
        implements DataMemoryRepository<O, P> /*, ObjectsPool<O>*/ {

    // * Если в наследнике сделать публичным {@link AbstractObjectsFactory}, это будет означать, что объекты обслуживаемого типа
    // * можно создавать. В классах элементов нужно перехватывать с помощью @JsonCreator момент создания
    // * и передать управление фабрике. Например, Инструмент (базовый) и наследники Бумаги, Валюты и Деривативы.
    //            * Тогда в репозитории Инструментов будет неуместно определять Фабрику, а в репозиториях Бумаг, Валют и Дериватов
    // * необходимо определить Фабрику, которая будет наследником от {@link AbstractObjectsFactory}. В классах Бумага, Валюта
    // * и Дериватив необходимо перехватить создание объекта @JsonCreate-ом и вызвать
    // * {@link AbstractObjectsFactory#getOrCreateObject}
    // * <p/>
    //            * В наследниках рекомендуется переопределить {@link #internalCreateEmptyInstance()}, в которой создавать объект
    // * оператором new() без вызова данной реализации через super.
    //            * <p/>

    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PROTECTED)
    private static final Map<Class<? extends AbstractMemoryRepository>, AbstractMemoryRepository> instancesByRepositoryClass = new HashMap<>();

    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.PROTECTED)
    private static final Map<Class<? extends AbstractDataObject>, AbstractMemoryRepository> instancesByObjectsClass = new HashMap<>();

    @SuppressWarnings("rawtypes")
    protected static AbstractMemoryRepository getRepositoryByClass(Class<? extends AbstractMemoryRepository> repositoryClass) {
        return instancesByRepositoryClass.get(repositoryClass);
    }

    @SuppressWarnings("rawtypes")
    protected static AbstractMemoryRepository getRepositoryByObjectsClass(Class<? extends AbstractDataObject> objectsClass) {
        return instancesByObjectsClass.get(objectsClass);
    }

    @Getter(AccessLevel.PROTECTED)
    private final ObjectMapper objectMapper;

    @Getter(AccessLevel.PROTECTED)
    private final Map<Object, O> objects = new HashMap<>();

    private Class<O> objectsClass;

    private Class<P> packagesClass;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "unchecked"})
    protected AbstractMemoryRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        this.objectMapper = objectMapper;

        final var thisClass = this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.objectsClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            this.packagesClass = (Class<P>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
        }

        synchronized (thisClass) {
            var instance = getRepositoryByClass(thisClass);
            if (instance != null) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered! Class = " + thisClass.getName());
            }
            instancesByRepositoryClass.put(thisClass, this);

            instance = getRepositoryByObjectsClass(this.objectsClass);
            if (instance != null) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered (by Objects Class)! ObjectsClass = " + this.objectsClass.getName());
            }
            instancesByObjectsClass.put(this.objectsClass, this);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="// реализация ObjectPool">
    //
    //    /**
    //     * Создание "заготовки" объекта. Вызывается Фабрикой объектов.
    //     * Рекомендуется переопределить (без вызова данной реализации через super) в наследнике
    //     * и там создавать нормально через оператор new().
    //     *
    //     * @return Объект репозитория.
    //     */
    //    protected O internalCreateEmptyInstance() throws ObjectCreateException {
    //        final var objectClass = getObjectClass();
    //        try {
    //            final var constructor = objectClass.getConstructor();
    //            return constructor.newInstance();
    //        } catch (Exception e) {
    //            throw new ObjectCreateException("Ошибка при создании экземпляра класса: " + objectClass.getName(), e);
    //        }
    //    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация DataMemoryRepository">
    /**
     * @return Класс объектов репозитория.
     */
    public Class<O> getObjectClass() {
        return this.objectsClass;
    }

    /**
     * @return Класс пакета объектов.
     */
    public Class<P> getPackageClass() {
        return this.packagesClass;
    }

    /**
     * @return Количество объектов в Репозитории.
     */
    @SuppressWarnings("unused")
    @Override
    public int size() {
        return this.objects.size();
    }

    private O putInternal(Object key, O object) {
        return getObjects().put(key, object);
    }

    /**
     * Добавление объекта в репозиторий.
     * @param object                            Добавляемый объект.
     * @throws ObjectAlreadyExistsException     Ошибка, если для ключа key уже зарегистрирован объект в репозитории.
     */
    @Override
    public void insert(O object) throws ObjectAlreadyExistsException {
        final var key = extractKey(object);
        if (!containsKey(key)) {
            putInternal(key, object);
        } else {
            throw new ObjectAlreadyExistsException(key, object);
        }
    }

    /**
     * Обновление объекта с ключом key. Обновляемый экземпляр не заменяется, а обновляются данные самого объекта.
     * @param object                        Новое состояние объекта.
     * @throws JsonMappingException         Ошибка при десериализации объекта в объект.
     * @throws ObjectNotExistsException     Ошибка, если для ключа key не зарегистрирован объект в репозитории.
     */
    @Override
    public void update(O object) throws JsonMappingException, ObjectNotExistsException {
        final var key = extractKey(object);
        final var oldObject = getByKey(key);
        if (oldObject != null) {
            if (!oldObject.equals(object)) {
                getObjectMapper().updateValue(oldObject, object);
            }
        } else {
            throw new ObjectNotExistsException(key, object);
        }
    }

    /**
     * Замена объекта с ключом key в репозитории.
     * @param object                    Новый объект, который заменит старый объект.
     * @return                          Предыдущий объект, который был ассоциирован с ключом key.
     * @throws ObjectNotExistsException Ошибка, если объект не найден в Репозитории.
     */
    @Override
    public O replace(O object) throws ObjectNotExistsException {
        final var key = extractKey(object);
        final var oldObject = getByKey(key);
        if (oldObject != null) {
            if (!oldObject.equals(object)) {
                putInternal(key, object);
                return oldObject;
            } else {
                return null;
            }
        } else {
            throw new ObjectNotExistsException(key, object);
        }
    }

    /**
     * Запись объекта object с ключом key в репозиторий.
     * @param object    Объект.
     * @return          Предыдущий объект с заданным ключом, если такой был.
     */
    @Override
    public O put(O object) {
        return putInternal(extractKey(object), object);
    }

    /**
     * Запись нескольких объектов с соответствующими ключами для них.
     * @param source    Map-а ключей и объектов.
     */
    @Override
    public void putAll(Collection<O> source) {
        source.forEach(s -> this.putInternal(extractKey(s), s));
    }

    /**
     * Удаление объекта из репозитория, который зарегистрирован для ключа key.
     * @param key       Ключ.
     * @return          Объект, если
     */
    @Override
    public O removeByKey(Object key) {
        return getObjects().remove(key);
    }

    /**
     * Удаление объекта object из репозитория, который зарегистрирован для ключа key.
     * @param object        Удаляемый объект.
     * @return              Удаленный объект, если с заданным ключом был объект, указанный в параметре object.
     */
    @Override
    public O remove(O object) {
        final var key = extractKey(object);
        final var result = getObjects().get(key);
        if (result != null && result.equals(object)) {
            return removeByKey(key);
        } else {
            return null;
        }
    }

    /**
     * Получение объекта по идентификатору (ключу), который указан у класса в @JsonIdentityInfo.
     *
     * @param key значение ключа, по которому ищем объект.
     * @return объект, если такой найден; null, если по такому ключу в IdResolver-е нет объекта.
     */
    @Override
    public O getByKey(Object key) {
        return this.getObjects().get(key);
    }

    /**
     * Проверка наличия объекта с указанным ключом в репозитории.
     * @param key Ключ.
     * @return true - объект есть, false - объекта нет.
     */
    @Override
    public boolean containsKey(Object key) {
        return this.objects.containsKey(key);
    }

    /**
     * Получение ключа объекта, по которому его идентифицирует данный MemoryRepository.
     * @param dataObject    Объект данных, из которого "извлекаем" ключ.
     * @return              Ключ, идентифицирующий указанный объект данных.
     */
    @Override
    public abstract Object extractKey(O dataObject);
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация Iterable">
    @Override
    public Iterator<O> iterator() {
        return this.objects.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super O> action) {
        this.objects.values().forEach(action);
    }

    @Override
    public Spliterator<O> spliterator() {
        return this.objects.values().spliterator();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ObjectIdResolver">
    @SuppressWarnings("unused")
    protected void bindItem(ObjectIdGenerator.IdKey id, Object pojo) {
        //        final var old = AbstractMemoryRepository.this.objects.get(id.key);
        //        if (old != null) {
        //            if (Objects.equals(old, pojo)) {
        //                return;
        //            }
        //            // TODO: Обновление объекта!
        //            throw new IllegalStateException("Already had POJO for id (" + id.key.getClass().getName() + ") [" + id + "]");
        //        }
        //        AbstractMemoryRepository.this.objects.put(id.key, (O) pojo);
    }

    protected Object resolveId(ObjectIdGenerator.IdKey id) {
        return AbstractMemoryRepository.this.objects.get(id.key);
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------

    /**
     * IdResolver нужен для определения объекта по его ключу - нужен для ObjectMapper-а jackson-а.
     * При десериализации объекта jackson также регистриует объект с ключом в IdResolver-е.
     * Это используется для наполнения списка объектов Репозитория {@link #objects}.
     */
    @SuppressWarnings("rawtypes")
    protected static abstract class AbstractIdResolver<O extends AbstractMemoryRepository> implements ObjectIdResolver {
        private AbstractMemoryRepository repository;

        private Class<? extends AbstractMemoryRepository> memoryRepositoryClass;

        @SuppressWarnings("unchecked")
        public AbstractIdResolver() {
            super();

            final var thisClass = this.getClass();
            final var superClass = thisClass.getGenericSuperclass();
            if (superClass != null) {
                this.memoryRepositoryClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            }
        }

        protected Class<? extends AbstractMemoryRepository> getRepositoryClass() {
            return this.memoryRepositoryClass;
        }

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

    //    /**
    //     * Фабрика объектов. Предназначена для выделения экземпляров объектов
    //     * с предварительной проверкой на существование объекта с таким же ключом.
    //     */
    //    @SuppressWarnings("unused")
    //    protected static abstract class AbstractObjectsFactory {
    //        /**
    //         * Если объект с таким ключом уже зарегистрирован в репозитории, то будет выдан этот существующий объект.
    //         * Если объекта с таким ключом нет, от выдается из пула свободная "заготовка"
    //         * (если в пуле закончились "заготовки", создается новый экземпляр).
    //         *
    //         * @param key Ключ, по которому ищется объект в Репозитории
    //         * @return Уже существующий и ранее зарегестрированный объект в Репозитории, или заготовка из Пула, или новый экземпляр.
    //         * @throws ObjectCreateException Ошибка при выделении объекта из Пула
    //         *                               (например, "заготовки" закончились, а создавать новый экземпляр запрещено).
    //         */
    //        @SuppressWarnings("unchecked")
    //        @Deprecated
    //        protected static <X extends AbstractDataObject> X getOrCreateObject(Class<X> objectClass, Object key) throws ObjectCreateException {
    //            final var owner = getRepositoryByObjectsClass(objectClass);
    //            var result = (X) owner.getByKey(key);
    //            if (result != null) {
    //                return result;
    //            }
    //
    //            result = (X) owner.internalCreateEmptyInstance();
    //            return result;
    //        }
    //    }
}
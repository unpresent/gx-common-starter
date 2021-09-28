package ru.gx.common.data;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Базовая непотокобезопасная реализация InMemory-репозитория объектов типа E.
 * <br/>
 * В поле {@link #objects} находятся объекты, с которыми сейчас работают.
 * // В {objectsPool} находятся заготовки объектов - свободные, которые выделяются при запросе у пула объекта.
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
        implements DataMemoryRepository<O, P> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @NotNull
    private static final Object monitor = new Object();

    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private static final Map<Class<? extends AbstractMemoryRepository<?, ?>>, AbstractMemoryRepository<?, ?>> instancesByRepositoryClass = new HashMap<>();

    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private static final Map<Class<? extends AbstractDataObject>, AbstractMemoryRepository<?, ?>> instancesByObjectsClass = new HashMap<>();

    @NotNull
    protected static AbstractMemoryRepository<?, ?> getRepositoryByClass(@NotNull final Class<? extends AbstractMemoryRepository<?, ?>> repositoryClass) {
        final var result = instancesByRepositoryClass.get(repositoryClass);
        if (result == null) {
            throw new InvalidParameterException("Repository " + repositoryClass.getSimpleName() + " not registered!");
        }
        return result;
    }

    @SuppressWarnings("unused")
    @NotNull
    protected static AbstractMemoryRepository<?, ?> getRepositoryByObjectsClass(@NotNull final Class<? extends AbstractDataObject> objectsClass) {
        final var result = instancesByObjectsClass.get(objectsClass);
        if (result == null) {
            throw new InvalidParameterException("ObjectClass " + objectsClass.getSimpleName() + " not registered!");
        }
        return result;
    }

    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private final ObjectMapper objectMapper;

    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private final Map<Object, O> objects = new HashMap<>();

    private Class<O> objectsClass;

    private Class<P> packagesClass;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    @SuppressWarnings({"unchecked"})
    protected AbstractMemoryRepository(@NotNull final ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException, InvalidParameterException {
        this.objectMapper = objectMapper;

        final Class<? extends AbstractMemoryRepository<?, ?>> thisClass = (Class<? extends AbstractMemoryRepository<?, ?>>)this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.objectsClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            this.packagesClass = (Class<P>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
        }
        if (this.objectsClass == null) {
            throw new InvalidParameterException("Can't create " + thisClass.getSimpleName() + " due undefined generic parameter[0].");
        }
        if (this.packagesClass == null) {
            throw new InvalidParameterException("Can't create " + thisClass.getSimpleName() + " due undefined generic parameter[1].");
        }

        synchronized (monitor) {
            if (instancesByRepositoryClass.containsKey(thisClass)) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered! Class = " + thisClass.getName());
            }
            if (instancesByObjectsClass.containsKey(this.objectsClass)) {
                throw new SingletonInstanceAlreadyExistsException("Singleton instance already registered (by Objects Class)! ObjectsClass = " + this.objectsClass.getName());
            }

            instancesByRepositoryClass.put(thisClass, this);
            instancesByObjectsClass.put(this.objectsClass, this);
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация DataMemoryRepository">
    /**
     * @return Класс объектов репозитория.
     */
    @SuppressWarnings("unused")
    @NotNull
    public Class<O> getObjectClass() {
        return this.objectsClass;
    }

    /**
     * @return Класс пакета объектов.
     */
    @SuppressWarnings("unused")
    @NotNull
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

    @Nullable
    private O putInternal(@NotNull final Object key, @NotNull final O object) {
        return getObjects().put(key, object);
    }

    /**
     * Добавление объекта в репозиторий.
     *
     * @param object Добавляемый объект.
     * @throws ObjectAlreadyExistsException Ошибка, если для ключа key уже зарегистрирован объект в репозитории.
     */
    @Override
    public void insert(@NotNull O object) throws ObjectAlreadyExistsException {
        final var key = extractKey(object);
        if (!containsKey(key)) {
            putInternal(key, object);
        } else {
            throw new ObjectAlreadyExistsException(key, object);
        }
    }

    /**
     * Обновление объекта с ключом key. Обновляемый экземпляр не заменяется, а обновляются данные самого объекта.
     *
     * @param object Новое состояние объекта.
     * @throws JsonMappingException     Ошибка при десериализации объекта в объект.
     * @throws ObjectNotExistsException Ошибка, если для ключа key не зарегистрирован объект в репозитории.
     */
    @Override
    public void update(@NotNull O object) throws JsonMappingException, ObjectNotExistsException {
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
     *
     * @param object Новый объект, который заменит старый объект.
     * @return Предыдущий объект, который был ассоциирован с ключом key.
     * @throws ObjectNotExistsException Ошибка, если объект не найден в Репозитории.
     */
    @Override
    @NotNull
    public O replace(@NotNull O object) throws ObjectNotExistsException {
        final var key = extractKey(object);
        final var oldObject = getByKey(key);
        if (oldObject != null) {
            if (!oldObject.equals(object)) {
                putInternal(key, object);
                return oldObject;
            } else {
                return object;
            }
        } else {
            throw new ObjectNotExistsException(key, object);
        }
    }

    /**
     * Запись объекта object с ключом key в репозиторий.
     *
     * @param object Объект.
     * @return Предыдущий объект с заданным ключом, если такой был.
     */
    @Override
    @Nullable
    public O put(@NotNull O object) {
        return putInternal(extractKey(object), object);
    }

    /**
     * Запись нескольких объектов с соответствующими ключами для них.
     *
     * @param source Map-а ключей и объектов.
     */
    @Override
    public void putAll(@NotNull Collection<O> source) {
        source.forEach(s -> this.putInternal(extractKey(s), s));
    }

    /**
     * Удаление объекта из репозитория, который зарегистрирован для ключа key.
     *
     * @param key Ключ.
     * @return Объект, если
     */
    @Override
    @Nullable
    public O removeByKey(@NotNull Object key) {
        return getObjects().remove(key);
    }

    /**
     * Удаление объекта object из репозитория, который зарегистрирован для ключа key.
     *
     * @param object Удаляемый объект.
     * @return Удаленный объект, если с заданным ключом был объект, указанный в параметре object.
     */
    @Override
    @Nullable
    public O remove(@NotNull O object) {
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
    @Nullable
    public O getByKey(@NotNull Object key) {
        return this.getObjects().get(key);
    }

    /**
     * Проверка наличия объекта с указанным ключом в репозитории.
     *
     * @param key Ключ.
     * @return true - объект есть, false - объекта нет.
     */
    @Override
    public boolean containsKey(@NotNull Object key) {
        return this.objects.containsKey(key);
    }

    /**
     * Получение ключа объекта, по которому его идентифицирует данный MemoryRepository.
     *
     * @param dataObject Объект данных, из которого "извлекаем" ключ.
     * @return Ключ, идентифицирующий указанный объект данных.
     */
    @Override
    @NotNull
    public abstract Object extractKey(@NotNull O dataObject);

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация Iterable">
    @Override
    @NotNull
    public Iterator<O> iterator() {
        return this.objects.values().iterator();
    }

    @Override
    public void forEach(@NotNull Consumer<? super O> action) {
        this.objects.values().forEach(action);
    }

    @Override
    @NotNull
    public Spliterator<O> spliterator() {
        return this.objects.values().spliterator();
    }

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="реализация ObjectIdResolver">
    @SuppressWarnings("unused")
    protected void bindItem(@NotNull final ObjectIdGenerator.IdKey id, @NotNull final Object pojo) {
    }

    protected Object resolveId(@NotNull final ObjectIdGenerator.IdKey id) {
        return AbstractMemoryRepository.this.objects.get(id.key);
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------

    /**
     * IdResolver нужен для определения объекта по его ключу - нужен для ObjectMapper-а jackson-а.
     * При десериализации объекта jackson также регистрирует объект с ключом в IdResolver-е.
     * Это используется для наполнения списка объектов Репозитория {@link #objects}.
     */
    protected static abstract class AbstractIdResolver<O extends AbstractMemoryRepository<?, ?>> implements ObjectIdResolver {
        private AbstractMemoryRepository<?, ?> repository;

        private Class<? extends AbstractMemoryRepository<?, ?>> memoryRepositoryClass;

        @SuppressWarnings("unchecked")
        public AbstractIdResolver() {
            super();

            final var thisClass = this.getClass();
            final var superClass = thisClass.getGenericSuperclass();
            if (superClass != null) {
                this.memoryRepositoryClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            }
        }

        @NotNull
        protected Class<? extends AbstractMemoryRepository<?, ?>> getRepositoryClass() {
            return this.memoryRepositoryClass;
        }

        @NotNull
        private AbstractMemoryRepository<?, ?> getRepository() {
            if (this.repository == null) {
                this.repository = getRepositoryByClass(getRepositoryClass());
            }
            return this.repository;
        }

        // -------------------------------------------------------------------------------------------------------------
        // <editor-fold desc="реализация ObjectIdResolver">
        @Override
        public void bindItem(@NotNull final ObjectIdGenerator.IdKey id, @NotNull final Object pojo) {
            getRepository().bindItem(id, pojo);
        }

        @Override
        public Object resolveId(@NotNull final ObjectIdGenerator.IdKey id) {
            return getRepository().resolveId(id);
        }

        @Override
        public boolean canUseFor(@NotNull final ObjectIdResolver resolverType) {
            return resolverType.getClass() == this.getClass();
        }

        @Override
        @NotNull
        public ObjectIdResolver newForDeserialization(@NotNull final Object context) {
            return this;
        }
        // </editor-fold>
        // -------------------------------------------------------------------------------------------------------------
    }
}
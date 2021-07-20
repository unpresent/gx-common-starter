package ru.gxfin.common.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Stack;

/**
 * Базовая реализация пула объектов для многопоточных решений.
 * Потокобезопасная реализация.
 * Объект, которые возвращается в пул - свободен для повторного использования. При его возврате осуществляется очистка объекта.
 * @param <T> Тип объектов, аправлемых данным пулом.
 */
@Accessors(chain = true)
public abstract class AbstractConcurrentObjectsPool<T extends PoolableObject> implements ObjectsPool<T> {
    /**
     * Признак допустимости создавать новые объекты после инициализации.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean allowCreateObjectsAfterInit;

    /**
     * Потокобезопасный стэк объектов в пуле (т.е. свободных для использования).
     */
    private final Stack<T> objects;

    /**
     * Конструктор пула объектов.
     * @param allowCreateObjectsAfterInit Признак допустимости создавать новые объекты после инициализации.
     * @param initSize Количество объектов, которое будет создано в процессе инициализации.
     * @throws ObjectsPoolException Исключение может возникнуть в результате создания объекта.
     */
    protected AbstractConcurrentObjectsPool(boolean allowCreateObjectsAfterInit, int initSize) throws ObjectsPoolException {
        this.allowCreateObjectsAfterInit = allowCreateObjectsAfterInit;
        this.objects = new Stack<>();
        for (int i = 0; i < initSize; i++) {
            this.objects.add(createInstance());
        }
    }

    /**
     * Получение объекта из пула. При его получении, он удаляется из списка свободных.
     * @return Чистый объект из пула объектов.
     * @throws ObjectsPoolException Ошибки при создании экземпляра объекта.
     */
    @Override
    public T pollObject() throws ObjectsPoolException {
        if (!objects.isEmpty()) {
            return this.objects.pop();
        } else if (this.allowCreateObjectsAfterInit) {
            return createInstance();
        }
        return null;
    }

    /**
     * Возврат объекта в пул, когда этот объект более не нужен в использовании.
     * При его возврате будет вызван метод очистки объекта от данных.
     * @see PoolableObject#cleanOnReleaseToPool
     * @param object Возвращаемый объект.
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Override
    public void releaseObject(T object) {
        synchronized (object) {
            object.cleanOnReleaseToPool();
            this.objects.push(object);
        }
    }

    /**
     * @return Количество свободных объектов.
     */
    @Override
    public int freeObjectsCount() {
        return this.objects.size();
    }

    /**
     * Процедура создания экземпляра объекта.
     * @return Экземпляр объекта.
     * @throws ObjectsPoolException Ошибки при создании экземпляра объекта.
     */
    protected abstract T createInstance() throws ObjectsPoolException;
}

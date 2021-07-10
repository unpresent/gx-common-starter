package ru.gxfin.common.data;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
    private boolean allowCreateObjects;

    /**
     * Потокобезопасный стэк объектов в пуле (т.е. свободных для использования).
     */
    private final Stack<T> objects;

    /**
     * Конструктор пула объектов.
     * @param allowCreateObjects Признак допустимости создавать новые объекты после инициализации.
     * @param initSize Количество объектов, которое будет создано в процессе инициализации.
     * @throws ObjectsPoolException Исключение может возникнуть в результате создания объекта.
     */
    protected AbstractConcurrentObjectsPool(boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
        this.allowCreateObjects = allowCreateObjects;
        if (!allowCreateObjects && initSize > 0) {
            throw new ObjectsPoolException("Cann`t create objects due allowCreateObjects == false; initSize == " + initSize);
        }

        this.objects = new Stack<>();
        for (int i = 0; i < initSize; i++) {
            this.objects.add(createObject());
        }
    }

    /**
     * Получение объекта из пула. При его получении, он удаляется из списка свободных.
     * @return Чистый объект из пула объектов.
     * @throws ObjectsPoolException
     */
    @Override
    public T pollObject() throws ObjectsPoolException {
        if (!objects.isEmpty()) {
            return this.objects.pop();
        } else if (this.allowCreateObjects) {
            return createObject();
        }
        return null;
    }

    /**
     * Возврат объекта в пул, когда этот объект более не нужен в использовании.
     * При его возврате будет вызван метод очистки объекта от данных.
     * @see PoolableObject#cleanOnReturnToPool
     * @param object Возвращаемый объект.
     */
    @Override
    public void returnObject(T object) {
        synchronized (object) {
            object.cleanOnReturnToPool();
            this.objects.push(object);
        }
    }

    @Override
    public int freeObjectsCount() {
        return this.objects.size();
    }

    protected abstract T createObject() throws ObjectsPoolException;
}

package ru.gxfin.common.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Базовая реализация пула объектов для однопоточных решений.
 * Не потокобезопасная реализация.
 * Объект, которые возвращается в пул - свободен для повторного использования. При его возврате осуществляется очистка объекта.
 * @param <T> Тип объектов, аправлемых данным пулом.
 */
public abstract class AbstractSimpleObjectsPool<T extends PoolableObject> implements ObjectsPool<T> {
    @Getter(AccessLevel.PROTECTED)
    private final Object owner;

    /**
     * Признак допустимости создавать новые объекты после инициализации.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean allowCreateObjectsAfterInit;

    /**
     * Список объектов в пуле (т.е. свободных для использования).
     */
    private final ArrayList<T> objects;

    /**
     * Конструктор пула объектов.
     * @param allowCreateObjectsAfterInit Признак допустимости создавать новые объекты после инициализации.
     * @param initSize Количество объектов, которое будет создано в процессе инициализации.
     * @throws ObjectsPoolException Исключение может возникнуть в результате создания объекта.
     */
    protected AbstractSimpleObjectsPool(Object owner, boolean allowCreateObjectsAfterInit, int initSize) throws ObjectsPoolException {
        this.owner = owner;
        this.allowCreateObjectsAfterInit = allowCreateObjectsAfterInit;
        this.objects = new ArrayList<>(initSize + 32);
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
            return this.objects.remove(this.objects.size() - 1);
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
    @Override
    public void releaseObject(T object) {
        object.cleanOnReleaseToPool();
        this.objects.add(object);
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
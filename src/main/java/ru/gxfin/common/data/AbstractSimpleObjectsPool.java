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
    /**
     * Признак допустимости создавать новые объекты после инициализации.
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private boolean allowCreateObjects;

    /**
     * Список объектов в пуле (т.е. свободных для использования).
     */
    private final ArrayList<T> objects;

    /**
     * Конструктор пула объектов.
     * @param allowCreateObjects Признак допустимости создавать новые объекты после инициализации.
     * @param initSize Количество объектов, которое будет создано в процессе инициализации.
     * @throws ObjectsPoolException Исключение может возникнуть в результате создания объекта.
     */
    protected AbstractSimpleObjectsPool(boolean allowCreateObjects, int initSize) throws ObjectsPoolException {
        this.allowCreateObjects = allowCreateObjects;
        this.objects = new ArrayList<>(initSize + 32);
        for (int i = 0; i < initSize; i++) {
            this.objects.add(createObject());
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
        object.cleanOnReturnToPool();
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
    protected abstract T createObject() throws ObjectsPoolException;
}
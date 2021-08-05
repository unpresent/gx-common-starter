package ru.gxfin.common.data;

/**
 * Интерфейс пула объектов.
 * @param <T> тип объектов, которым управляет пул.
 */
@SuppressWarnings("unused")
@Deprecated
public interface ObjectsPool<T extends PoolableObject> {
    /**
     * Получение объекта из пула. При его получении, он удаляется из списка свободных.
     * @return Чистый объект типа T из пула объектов.
     * @throws ObjectCreateException Ошибки при создании экземпляра объекта.
     */
    T pollObject() throws ObjectCreateException;

    /**
     * Возврат объекта в пул, когда этот объект более не нужен в использовании.
     * При его возврате будет вызван метод очистки объекта от данных.
     * @see PoolableObject#cleanOnReleaseToPool
     * @param object Возвращаемый объект.
     */
    void releaseObject(T object);

    /**
     * @return Количество свободных объектов.
     */
    int freeObjectsCount();
}

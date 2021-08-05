package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Интерфейс InMemory-репозитория объектов наследников DataObjects
 * @param <O> тип Data Objects, которые обрабатывает данные репозиторий
 */
public interface DataMemoryRepository<O extends DataObject, P extends DataPackage<O>> extends Iterable<O> {

    /**
     * Десериализация json-а в объект. При этом объект регистрируется в репозитории.
     * @param jsonObject json-строка с объектом
     * @return объект в виде DataObject
     */
    @SuppressWarnings("unused")
    O loadObject(String jsonObject) throws JsonProcessingException;

    /**
     * Десериализация json-а в пакет объектов. При этом объекты регистрируется в репозитории.
     * @param jsonPackage json-строка с пакетом объектов
     * @return пакет объектов в виде DataPackage
     */
    @SuppressWarnings("unused")
    P loadPackage(String jsonPackage) throws JsonProcessingException;

    /**
     * Получение объекта по иденификатору (ключу), который указан у класса в @JsonIdentityInfo.
     * @param key значение ключа, по которому ищем объект.
     * @return объект, если такой найден; null, если по такому ключу в IdResolver-е нет объекта.
     */
    @SuppressWarnings("unused")
    O getByKey(Object key);

    /**
     * Проверка наличия объекта с указанным ключом в репозитории.
     * @param key Ключ.
     * @return true - объект есть, false - объекта нет.
     */
    @SuppressWarnings("unused")
    boolean containsKey(Object key);
}

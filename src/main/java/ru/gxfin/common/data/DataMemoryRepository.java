package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Интерфейс InMemory-репозитория объектов наследников DataObjects
 * @param <T> тип Data Objects, которые обрабатывает данные репозиторий
 */
public interface DataMemoryRepository<T extends DataObject> extends Iterable<T> {

    /**
     * Десериализация json-а в объект
     * @param jsonObject json-строка с объектом
     * @return объект в виде DataObject
     */
    @SuppressWarnings("unused")
    T deserializeObject(String jsonObject) throws JsonProcessingException;

    /**
     * Десериализация json-а в пакет объектов
     * @param jsonPackage json-строка с пакетом объектов
     * @return пакет объектов в виде DataPackage
     */
    @SuppressWarnings("unused")
    DataPackage<T> deserializePackage(String jsonPackage) throws JsonProcessingException;

    /**
     * Получение объекта по иденификатору (ключу), который указан у класса в @JsonIdentityInfo.
     * @param key значение ключа, по которому ищем объект.
     * @return объект, если такой найден; null, если по такому ключу в IdResolver-е нет объекта.
     */
    @SuppressWarnings("unused")
    T getByKey(Object key);
}

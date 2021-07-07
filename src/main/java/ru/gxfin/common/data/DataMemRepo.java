package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Интерфейс Memory репозитория с Data Objects
 * @param <T> тип Data Objects, которые обрабатывает данные репозиторий
 */
public interface DataMemRepo<T extends DataObject> {

    /**
     * Десериализация json-а в объект
     * @param jsonObject json-строка с объектом
     * @return объект в виде DataObject
     */
    T deserializeObject(String jsonObject) throws JsonProcessingException;

    /**
     * Десериализация json-а в пакет объектов
     * @param jsonPackage json-строка с пакетом объектов
     * @return пакет объектов в виде DataPackage
     */
    DataPackage<T> deserializePackage(String jsonPackage) throws JsonProcessingException;
}

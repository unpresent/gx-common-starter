package ru.gx.core.channels;

/**
 * Режим сериализации данных в канале
 */
public enum SerializeMode {

    /**
     * Объекты сериализованны в канале в виде Json-строк.
     */
    JsonString,

    /**
     * Объекты сериализованны в канале массива байт.
     */
    Bytes
}

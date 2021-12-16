package ru.gx.core.channels;

/**
 * Режим сериализации данных в канале
 */
public enum SerializeMode {

    /**
     * Объекты сериализованы в канале в виде Json-строк.
     */
    JsonString,

    /**
     * Объекты сериализованы в канале массива байт.
     */
    Bytes
}

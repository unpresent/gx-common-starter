package ru.gx.core.periodic;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.time.LocalDate;

@SuppressWarnings("unused")
public class PeriodicKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(final String key, final DeserializationContext context)
    {
        return LocalDate.parse(key);
    }
}
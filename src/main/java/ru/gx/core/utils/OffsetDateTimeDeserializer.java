package ru.gx.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {

    public static final OffsetDateTimeDeserializer INSTANCE = new OffsetDateTimeDeserializer();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm[:ss[.SSS]]xx");

    public OffsetDateTimeDeserializer() {
        this(null);
    }

    public OffsetDateTimeDeserializer(Class<OffsetDateTime> t) {
        super(t);
    }

    @Override
    public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final var stringValue = jsonParser.getValueAsString();
        return OffsetDateTime.parse(stringValue, DATETIME_FORMATTER);
    }
}
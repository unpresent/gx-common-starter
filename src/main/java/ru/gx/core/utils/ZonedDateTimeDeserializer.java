package ru.gx.core.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeDeserializer extends StdDeserializer<ZonedDateTime> {

    public static final ZonedDateTimeDeserializer INSTANCE = new ZonedDateTimeDeserializer();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[:ss[.SSS]]X");

    public ZonedDateTimeDeserializer() {
        this(null);
    }

    public ZonedDateTimeDeserializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final var stringValue = jsonParser.getValueAsString();
        return ZonedDateTime.parse(stringValue, DATETIME_FORMATTER);
    }
}
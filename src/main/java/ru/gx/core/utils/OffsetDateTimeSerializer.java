package ru.gx.core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {

    public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxx");

    public OffsetDateTimeSerializer() {
        this(null);
    }

    public OffsetDateTimeSerializer(Class<OffsetDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        final var result = value.format(DATETIME_FORMATTER);
        gen.writeString(result);
    }
}
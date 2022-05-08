package ru.gx.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gx.core.messaging.*;
import ru.gx.core.utils.ZonedDateTimeDeserializer;
import ru.gx.core.utils.ZonedDateTimeSerializer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.TimeZone;
import java.util.UUID;

@Testable
public class TestMessaging {
    public static final String M1 = """
                {
                    "header": {
                        "id": "0efa3fe6-1651-4d79-b554-4dd22308e9f7",
                        "kind": "Request",
                        "type": "TEST:TEST",
                        "sourceSystem": "TEST-SOURCE-SYSTEM",
                        "createdDateTime": "2021-12-19T09:36:49.456+03",
                        "version": 1
                    },
                    "body": {
                        "testDto": {
                            "code": "Code1",
                            "name": "Name1",
                            "price": 42.0,
                            "number": 11
                        }
                    },
                    "correlation": {
                        "k1": "v1",
                        "k2": "v2"
                    }
                }
            """;


    public static ObjectMapper newObjectMapper() {
        final var objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getDefault());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        final var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        javaTimeModule.addDeserializer(ZonedDateTime.class, ZonedDateTimeDeserializer.INSTANCE);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @SneakyThrows
    @Test
    public void doTestSerializeMessaging() {
        final var objectMapper = newObjectMapper();

        final var d1 = new TestDto("Code1", "Name1", BigDecimal.valueOf(42.0), (long) 11);
        final var correlation = new MessageCorrelation();
        correlation.put("k1", "v1");
        correlation.put("k2", "v2");

        final var m1 = new TestRequest1(
                new MessageHeader(UUID.randomUUID().toString(), null, MessageKind.Request, TestRequest1.MESSAGE_TYPE, TestRequest1.VERSION,"TEST-SOURCE-SYSTEM", OffsetDateTime.now()),
                new TestRequest1.TestRequest1Body(d1),
                correlation
        );

        final var s = objectMapper.writeValueAsString(m1);
        System.out.println(s);

        final var m3 = objectMapper.readValue(M1, TestRequest1.class);
        System.out.println("m3:");
        System.out.println(m3);

    }
}

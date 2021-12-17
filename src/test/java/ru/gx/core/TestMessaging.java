package ru.gx.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gx.core.messaging.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Testable
public class TestMessaging {
    public static final String M1 = """
            {
                "header": {
                    "id": "f9d9041a-edc5-4204-94cc-255ad9269c24",
                    "type": "TEST:TEST",
                    "sourceSystem": "FICS",
                    "createdDateTime": "2021-12-13 12:50:05.587",
                    "kind": "Request"
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
    public static final String M2 = """
            {
                "header": {
                    "id": "f9d9041a-edc5-4204-94cc-255ad9269c24"
                }
            }
            """;

    public static ObjectMapper newObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
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
                UUID.randomUUID().toString(),
                "TEST-SOURCE-SYSTEM",
                LocalDateTime.now(),
                d1,
                correlation
        );

        final var s = objectMapper.writeValueAsString(m1);
        System.out.println(s);

        //        final var m2 = objectMapper.readValue(M2, TestDes2.class);
        //        System.out.println("m2:");
        //        System.out.println(m2);

        final var m3 = objectMapper.readValue(s, TestRequest1.class);
        System.out.println("m3:");
        System.out.println(m3);
        System.out.println("m3.getBody():");
        System.out.println(m3.getBody());

    }
}

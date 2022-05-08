package ru.gx.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class TestJson {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    private final static String testJson = """
                {
                "bbb": 111,
                "aaa": {
                        "id": "MICEX:4534426269",
                        "rowIndex": 488350,
                        "tradeNum": "4534426269",
                        "direction": "B",
                        "value": 107.34
                        },
                "fp": "12312321"}
                """;

    @SuppressWarnings("unused")
    private final static String testJsonPackage = """
                {
                "allCount": 4047673,
                "objects": [{
                        "id": "MICEX:4534426269",
                        "rowIndex": 488350,
                        "tradeNum": "4534426269",
                        "direction": "B",
                        "tradeDateTime": [
                            2021,
                            10,
                            20,
                            10,
                            58,
                            35,
                            650000000
                        ],
                        "exchangeCode": "MICEX",
                        "classCode": "TQTF",
                        "secCode": "TRUR",
                        "price": 6.314,
                        "quantity": 17.0,
                        "value": 107.34,
                        "accruedInterest": 0.0,
                        "yield": 0.0,
                        "settleCode": "Y2",
                        "repoRate": 0.0,
                        "repoValue": 0.0,
                        "repo2Value": 0.0,
                        "repoTerm": 0,
                        "period": 1,
                        "openInterest": 0
                    }, {
                        "id": "MICEX:4534426270",
                        "rowIndex": 488351,
                        "tradeNum": "4534426270",
                        "direction": "B",
                        "tradeDateTime": [
                            2021,
                            10,
                            20,
                            10,
                            58,
                            35,
                            654000000
                        ],
                        "exchangeCode": "MICEX",
                        "classCode": "TQTF",
                        "secCode": "TRUR",
                        "price": 6.314,
                        "quantity": 5.0,
                        "value": 31.57,
                        "accruedInterest": 0.0,
                        "yield": 0.0,
                        "settleCode": "Y2",
                        "repoRate": 0.0,
                        "repoValue": 0.0,
                        "repo2Value": 0.0,
                        "repoTerm": 0,
                        "period": 1,
                        "openInterest": 0
                    }, {
                        "id": "MICEX:4534426271",
                        "rowIndex": 488352,
                        "tradeNum": "4534426271",
                        "direction": "B",
                        "tradeDateTime": [
                            2021,
                            10,
                            20,
                            10,
                            58,
                            35,
                            658000000
                        ],
                        "exchangeCode": "MICEX",
                        "classCode": "TQBR",
                        "secCode": "RUAL",
                        "price": 79.91,
                        "quantity": 2.0,
                        "value": 1598.2,
                        "accruedInterest": 0.0,
                        "yield": 0.0,
                        "settleCode": "Y2",
                        "repoRate": 0.0,
                        "repoValue": 0.0,
                        "repo2Value": 0.0,
                        "repoTerm": 0,
                        "period": 1,
                        "openInterest": 0
                    }
                ]
            }""";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("unused")
    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @SneakyThrows
    @Test
    public void testDeser() {
        var objectMapper = new ObjectMapper();

        var rootNode = objectMapper.readTree(testJson);
        System.out.println(rootNode.toString());

    }
}

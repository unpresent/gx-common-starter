package ru.gx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gx.json.TestJsonPackage;

@Testable
public class TestJson {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">
    private final static String testJsonPackage =  "{\n" +
            "    \"allCount\": 4047673,\n" +
            "    \"objects\": [{\n" +
            "            \"id\": \"MICEX:4534426269\",\n" +
            "            \"rowIndex\": 488350,\n" +
            "            \"tradeNum\": \"4534426269\",\n" +
            "            \"direction\": \"B\",\n" +
            "            \"tradeDateTime\": [\n" +
            "                2021,\n" +
            "                10,\n" +
            "                20,\n" +
            "                10,\n" +
            "                58,\n" +
            "                35,\n" +
            "                650000000\n" +
            "            ],\n" +
            "            \"exchangeCode\": \"MICEX\",\n" +
            "            \"classCode\": \"TQTF\",\n" +
            "            \"secCode\": \"TRUR\",\n" +
            "            \"price\": 6.314,\n" +
            "            \"quantity\": 17.0,\n" +
            "            \"value\": 107.34,\n" +
            "            \"accruedInterest\": 0.0,\n" +
            "            \"yield\": 0.0,\n" +
            "            \"settleCode\": \"Y2\",\n" +
            "            \"repoRate\": 0.0,\n" +
            "            \"repoValue\": 0.0,\n" +
            "            \"repo2Value\": 0.0,\n" +
            "            \"repoTerm\": 0,\n" +
            "            \"period\": 1,\n" +
            "            \"openInterest\": 0\n" +
            "        }, {\n" +
            "            \"id\": \"MICEX:4534426270\",\n" +
            "            \"rowIndex\": 488351,\n" +
            "            \"tradeNum\": \"4534426270\",\n" +
            "            \"direction\": \"B\",\n" +
            "            \"tradeDateTime\": [\n" +
            "                2021,\n" +
            "                10,\n" +
            "                20,\n" +
            "                10,\n" +
            "                58,\n" +
            "                35,\n" +
            "                654000000\n" +
            "            ],\n" +
            "            \"exchangeCode\": \"MICEX\",\n" +
            "            \"classCode\": \"TQTF\",\n" +
            "            \"secCode\": \"TRUR\",\n" +
            "            \"price\": 6.314,\n" +
            "            \"quantity\": 5.0,\n" +
            "            \"value\": 31.57,\n" +
            "            \"accruedInterest\": 0.0,\n" +
            "            \"yield\": 0.0,\n" +
            "            \"settleCode\": \"Y2\",\n" +
            "            \"repoRate\": 0.0,\n" +
            "            \"repoValue\": 0.0,\n" +
            "            \"repo2Value\": 0.0,\n" +
            "            \"repoTerm\": 0,\n" +
            "            \"period\": 1,\n" +
            "            \"openInterest\": 0\n" +
            "        }, {\n" +
            "            \"id\": \"MICEX:4534426271\",\n" +
            "            \"rowIndex\": 488352,\n" +
            "            \"tradeNum\": \"4534426271\",\n" +
            "            \"direction\": \"B\",\n" +
            "            \"tradeDateTime\": [\n" +
            "                2021,\n" +
            "                10,\n" +
            "                20,\n" +
            "                10,\n" +
            "                58,\n" +
            "                35,\n" +
            "                658000000\n" +
            "            ],\n" +
            "            \"exchangeCode\": \"MICEX\",\n" +
            "            \"classCode\": \"TQBR\",\n" +
            "            \"secCode\": \"RUAL\",\n" +
            "            \"price\": 79.91,\n" +
            "            \"quantity\": 2.0,\n" +
            "            \"value\": 1598.2,\n" +
            "            \"accruedInterest\": 0.0,\n" +
            "            \"yield\": 0.0,\n" +
            "            \"settleCode\": \"Y2\",\n" +
            "            \"repoRate\": 0.0,\n" +
            "            \"repoValue\": 0.0,\n" +
            "            \"repo2Value\": 0.0,\n" +
            "            \"repoTerm\": 0,\n" +
            "            \"period\": 1,\n" +
            "            \"openInterest\": 0\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @SneakyThrows(JsonProcessingException.class)
    @Test
    public void testParseOnlyHeaderPackage() {
        // final var objectMapper = getObjectMapper();
        // final var pack = objectMapper.readValue(testJsonPackage, TestJsonPackage.class);
        // System.out.println(pack);
    }
}

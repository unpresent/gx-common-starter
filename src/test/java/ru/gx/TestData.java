package ru.gx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gx.data.TestDataObject;
import ru.gx.data.TestObjectsRepository;
import ru.gx.data.TestDictionaryObject;
import ru.gx.data.TestDictionaryRepository;

@SuppressWarnings("unused")
@Testable
public class TestData {
    @SneakyThrows
    @Test
    public void TestMemRepo() {
        // Должно создаваться в Config-ах приложения
        final var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        final var testDictRepo = new TestDictionaryRepository();
        testDictRepo.init();
        testDictRepo.setObjectMapper(objectMapper);

        final var testMemRepo = new TestObjectsRepository();
        testMemRepo.init();
        testMemRepo.setObjectMapper(objectMapper);

        final var c = TestDataObject.class;

        final var dictObjJsonString1 = "{\"code\":\"X\",\"name\":\"Хипулька\"}";
        final var dictObjJsonString2 = "{\"code\":\"Y\",\"name\":\"Упулька\"}";

        final var d1 = objectMapper.readValue(dictObjJsonString1, TestDictionaryObject.class);
        testDictRepo.insert(d1);
        System.out.println(d1);

        final var d2 = objectMapper.readValue(dictObjJsonString2, TestDictionaryObject.class);
        testDictRepo.insert(d2);
        System.out.println(d2);

        final var jsonString1 = "{\"id\":1,\"code\":\"A\",\"name\":\"Имя мое есть Царь!\",\"dictionaryObject\":\"X\",\"timeVal\":\"1:08:43\"}";
        final var jsonString2 = "{\"id\":1,\"code\":\"A+\",\"name\":null,\"dictionaryObject\":\"Y\"}";
        final var jsonString3 = "{\"id\":2,\"code\":\"X\",\"name\":\"***\",\"dictionaryObject\":\"Y\"}";

        final var o1 = objectMapper.readValue(jsonString1, TestDataObject.class);
        testMemRepo.insert(o1);
        System.out.println(o1);

        final var o2 = objectMapper.readValue(jsonString2, TestDataObject.class);
        testMemRepo.update(o2);
        System.out.println(o2);

        if (o1 == testMemRepo.getByKey(testMemRepo.extractKey(o2))) {
            System.out.println("Совпали!");
        }
    }
}

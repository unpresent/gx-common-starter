package ru.gx.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import ru.gx.common.data.TestDataObject;
import ru.gx.common.data.TestObjectsRepository;
import ru.gx.common.data.TestDictionaryObject;
import ru.gx.common.data.TestDictionaryRepository;

@SuppressWarnings("unused")
@Testable
public class TestData {
    @SneakyThrows
    @Test
    public void TestMemRepo() {
        // Должно создаваться в Config-ах приложения
        final var objectMapper = new ObjectMapper();
        final var testDictRepo = new TestDictionaryRepository(objectMapper);
        final var testMemRepo = new TestObjectsRepository(objectMapper);

        final var c = TestDataObject.class;

        final var dictObjJsonString1 = "{\"code\":\"X\",\"name\":\"Хипулька\"}";
        final var dictObjJsonString2 = "{\"code\":\"Y\",\"name\":\"Упулька\"}";

        final var d1 = objectMapper.readValue(dictObjJsonString1, TestDictionaryObject.class);
        testDictRepo.insert(d1);
        System.out.println(d1);

        final var d2 = objectMapper.readValue(dictObjJsonString2, TestDictionaryObject.class);
        testDictRepo.insert(d2);
        System.out.println(d2);

        final var jsonString1 = "{\"id\":1,\"code\":\"A\",\"name\":\"Имя мое есть Царь!\",\"dictionaryObject\":\"X\"}";
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

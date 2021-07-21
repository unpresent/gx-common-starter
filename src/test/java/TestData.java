import com.fasterxml.jackson.databind.ObjectMapper;
import data.TestDictionaryRepository;
import data.TestObjectRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class TestData {
    @SneakyThrows
    @Test
    public void TestMemRepo() {
        // Должно создаваться в Config-ах приложения
        final var objectMapper = new ObjectMapper();
        final var testDictRepo = new TestDictionaryRepository(objectMapper, false,4);
        final var testMemRepo = new TestObjectRepository(objectMapper, false,4);

        final var dictObjJsonString1 = "{\"code\":\"X\",\"name\":\"Хипулька\"}";
        final var dictObjJsonString2 = "{\"code\":\"Y\",\"name\":\"Упулька\"}";

        final var d1 = testDictRepo.loadObject(dictObjJsonString1);
        System.out.println(d1);

        final var d2 = testDictRepo.loadObject(dictObjJsonString2);
        System.out.println(d2);

        final var jsonString1 = "{\"id\":1,\"code\":\"A\",\"name\":\"Имя мое есть Царь!\",\"dictionaryObject\":\"X\"}";
        final var jsonString2 = "{\"id\":1,\"code\":\"A+\",\"name\":null,\"dictionaryObject\":\"Y\"}";

        final var o1 = testMemRepo.loadObject(jsonString1);
        System.out.println(o1);

        final var o2 = testMemRepo.loadObject(jsonString2);
        System.out.println(o2);

        if (o1 == o2) {
            System.out.println("Совпали!");
        }
    }
}

import com.fasterxml.jackson.databind.ObjectMapper;
import data.TestDataObject;
import data.TestDictionaryRepository;
import data.TestObjectsRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

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

        final var d1 = testDictRepo.loadObject(dictObjJsonString1);
        System.out.println(d1);

        final var d2 = testDictRepo.loadObject(dictObjJsonString2);
        System.out.println(d2);

        final var jsonString1 = "{\"id\":1,\"code\":\"A\",\"name\":\"Имя мое есть Царь!\",\"dictionaryObject\":\"X\"}";
        final var jsonString2 = "{\"id\":1,\"code\":\"A+\",\"name\":null,\"dictionaryObject\":\"Y\"}";
        final var jsonString3 = "{\"id\":2,\"code\":\"X\",\"name\":\"***\",\"dictionaryObject\":\"Y\"}";

        final var o1 = testMemRepo.loadObject(jsonString1);
        System.out.println(o1);

        final var o2 = testMemRepo.loadObject(jsonString2);
        System.out.println(o2);

        if (o1 == o2) {
            System.out.println("Совпали!");
        }

        final var o3 = testMemRepo.loadObject(jsonString3);
        System.out.println(o3);

        final var oc = o1.getClass().getGenericSuperclass();
        final var ois = o1.getClass().getGenericInterfaces();
        if (ois.length > 0) {
            final var oi0 = ois[0];
        }
        if (ois.length > 1) {
            final var oi1 = ois[1];
        }
        final var mc = testMemRepo.getClass().getGenericSuperclass();
        final var mis = testMemRepo.getClass().getGenericInterfaces();
        if (mis.length > 0) {
            final var mi0 = mis[0];
        }
        if (mis.length > 1) {
            final var mi1 = mis[1];
        }
    }
}

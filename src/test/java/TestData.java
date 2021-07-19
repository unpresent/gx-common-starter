import com.fasterxml.jackson.databind.ObjectMapper;
import data.TestMemorySimpleRepository;
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
        final var testMemRepo = new TestMemorySimpleRepository(objectMapper, false, 4);

        final var jsonString1 = "{\"id\":1,\"code\":\"A\"}";
        final var jsonString2 = "{\"id\":1,\"code\":\"A+\"}";

        final var o1 = testMemRepo.loadObject(jsonString1);
        System.out.println(o1);

        final var o2 = testMemRepo.loadObject(jsonString2);
        System.out.println(o2);

        if (o1 == o2) {
            System.out.println("Совпали!");
        }
    }
}

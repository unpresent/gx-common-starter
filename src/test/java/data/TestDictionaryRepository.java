package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    public TestDictionaryRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    public Object extractKey(TestDictionaryObject dataObject) {
        return dataObject.getCode();
    }

    public static class IdResolver extends AbstractIdResolver<TestDictionaryRepository> {
    }
}

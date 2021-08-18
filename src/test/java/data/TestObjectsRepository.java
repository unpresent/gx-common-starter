package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestObjectsRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {
    public TestObjectsRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    public Object extractKey(TestDataObject dataObject) {
        return dataObject.getId();
    }

    public static class IdResolver extends AbstractIdResolver<TestObjectsRepository> {
    }
}

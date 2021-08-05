package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.ObjectCreateException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestObjectsRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {
    public TestObjectsRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    protected TestDataObject internalCreateEmptyInstance() {
        return new TestDataObject();
    }

    public static class IdResolver extends AbstractIdResolver {
        @SuppressWarnings("rawtypes")
        @Override
        protected Class<? extends AbstractMemoryRepository> getRepositoryClass() {
            return TestObjectsRepository.class;
        }
    }

    public static class ObjectFactory extends AbstractObjectsFactory {
        public static TestDataObject getOrCreateObject(int id) throws ObjectCreateException {
            return AbstractObjectsFactory.getOrCreateObject(TestDataObject.class, id);
        }
    }
}

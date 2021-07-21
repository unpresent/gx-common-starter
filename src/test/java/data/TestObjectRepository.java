package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.ObjectsPoolException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestObjectRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {
    public TestObjectRepository(ObjectMapper objectMapper, boolean isConcurrent, int initSize) throws SingletonInstanceAlreadyExistsException, ObjectsPoolException {
        super(objectMapper, isConcurrent, initSize);
    }

    @Override
    public Class<TestDataObject> getObjectClass() {
        return TestDataObject.class;
    }

    @Override
    public Class<TestDataPackage> getPackageClass() {
        return TestDataPackage.class;
    }

    @Override
    protected TestDataObject internalCreateEmptyInstance() {
        return new TestDataObject();
    }

    public static class IdResolver extends AbstractIdResolver {
        @SuppressWarnings("rawtypes")
        @Override
        protected Class<? extends AbstractMemoryRepository> getRepositoryClass() {
            return TestObjectRepository.class;
        }
    }

    public static class ObjectFactory extends AbstractObjectsFactory {
        public static TestDataObject getOrCreateObject(int id) throws ObjectsPoolException {
            return AbstractObjectsFactory.getOrCreateObject(TestDataObject.class, id);
        }
    }
}

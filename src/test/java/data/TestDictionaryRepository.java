package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.ObjectsPoolException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    public TestDictionaryRepository(ObjectMapper objectMapper, boolean isConcurrent, int initSize) throws SingletonInstanceAlreadyExistsException, ObjectsPoolException {
        super(objectMapper, isConcurrent, initSize);
    }

    @Override
    public Class<TestDictionaryObject> getObjectClass() {
        return TestDictionaryObject.class;
    }

    @Override
    public Class<TestDictionaryPackage> getPackageClass() {
        return TestDictionaryPackage.class;
    }

    @Override
    protected TestDictionaryObject internalCreateEmptyInstance() {
        return new TestDictionaryObject();
    }

    public static class IdResolver extends AbstractIdResolver {
        @SuppressWarnings("rawtypes")
        @Override
        protected Class<? extends AbstractMemoryRepository> getRepositoryClass() {
            return TestDictionaryRepository.class;
        }
    }

    public static class ObjectFactory extends AbstractObjectsFactory {
        public static TestDictionaryObject getOrCreateObject(String code) throws ObjectsPoolException {
            return AbstractObjectsFactory.getOrCreateObject(TestDictionaryObject.class, code);
        }
    }
}

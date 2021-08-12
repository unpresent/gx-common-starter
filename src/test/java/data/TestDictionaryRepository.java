package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.ObjectCreateException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExistsException;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    public TestDictionaryRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    protected TestDictionaryObject internalCreateEmptyInstance() {
        return new TestDictionaryObject();
    }

    @Override
    public Object extractKey(TestDictionaryObject dataObject) {
        return dataObject.getCode();
    }

    public static class IdResolver extends AbstractIdResolver {
        @SuppressWarnings("rawtypes")
        @Override
        protected Class<? extends AbstractMemoryRepository> getRepositoryClass() {
            return TestDictionaryRepository.class;
        }
    }

    public static class ObjectFactory extends AbstractObjectsFactory {
        public static TestDictionaryObject getOrCreateObject(String code) throws ObjectCreateException {
            return AbstractObjectsFactory.getOrCreateObject(TestDictionaryObject.class, code);
        }
    }
}

package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemoryRepository;
import ru.gxfin.common.data.ObjectsPoolException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExists;

public class TestMemorySimpleRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {
    public TestMemorySimpleRepository(ObjectMapper objectMapper, boolean isConcurrent, int initSize) throws SingletonInstanceAlreadyExists, ObjectsPoolException {
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

    public class ObjectsFactory extends AbstractObjectsFactory {
    }

    public static class IdResolver extends AbstractIdResolver {
    }
}

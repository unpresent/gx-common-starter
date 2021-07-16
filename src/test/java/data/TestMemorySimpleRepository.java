package data;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.gxfin.common.data.AbstractMemorySimpleRepository;
import ru.gxfin.common.data.ObjectsPoolException;
import ru.gxfin.common.data.SingletonInstanceAlreadyExists;

public class TestMemorySimpleRepository extends AbstractMemorySimpleRepository<TestDataObject, TestDataPackage> {
    public TestMemorySimpleRepository(ObjectMapper objectMapper, int initSize) throws SingletonInstanceAlreadyExists, ObjectsPoolException {
        super(objectMapper, initSize);
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

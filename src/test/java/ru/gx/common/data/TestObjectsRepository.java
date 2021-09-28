package ru.gx.common.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestObjectsRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {
    public TestObjectsRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    @NotNull
    public Object extractKey(@NotNull TestDataObject dataObject) {
        return dataObject.getId();
    }

    public static class IdResolver extends AbstractIdResolver<TestObjectsRepository> {
    }
}


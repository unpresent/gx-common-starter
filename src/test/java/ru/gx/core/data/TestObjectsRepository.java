package ru.gx.core.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestObjectsRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {

    public TestObjectsRepository(@NotNull final ObjectMapper objectMapper) {
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


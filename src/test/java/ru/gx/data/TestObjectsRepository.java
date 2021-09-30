package ru.gx.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestObjectsRepository extends AbstractMemoryRepository<TestDataObject, TestDataPackage> {

    @Override
    @NotNull
    public Object extractKey(@NotNull TestDataObject dataObject) {
        return dataObject.getId();
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        super.setObjectMapper(objectMapper);
    }

    public static class IdResolver extends AbstractIdResolver<TestObjectsRepository> {
    }
}


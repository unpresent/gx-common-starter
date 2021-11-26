package ru.gx.core.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    @Override
    @NotNull
    public Object extractKey(@NotNull TestDictionaryObject dataObject) {
        return dataObject.getCode();
    }

    @Override
    public void setObjectMapper(ObjectMapper objectMapper) {
        super.setObjectMapper(objectMapper);
    }

    public static class IdResolver extends AbstractIdResolver<TestDictionaryRepository> {
    }
}

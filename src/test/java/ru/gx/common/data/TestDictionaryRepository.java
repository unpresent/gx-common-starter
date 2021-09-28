package ru.gx.common.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    public TestDictionaryRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    @NotNull
    public Object extractKey(@NotNull TestDictionaryObject dataObject) {
        return dataObject.getCode();
    }

    public static class IdResolver extends AbstractIdResolver<TestDictionaryRepository> {
    }
}

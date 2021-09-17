package ru.gxfin.common.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class TestDictionaryRepository extends AbstractMemoryRepository<TestDictionaryObject, TestDictionaryPackage> {
    public TestDictionaryRepository(ObjectMapper objectMapper) throws SingletonInstanceAlreadyExistsException {
        super(objectMapper);
    }

    @Override
    public Object extractKey(@NotNull TestDictionaryObject dataObject) {
        return dataObject.getCode();
    }

    public static class IdResolver extends AbstractIdResolver<TestDictionaryRepository> {
    }
}

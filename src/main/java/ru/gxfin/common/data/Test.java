package ru.gxfin.common.data;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;

public class Test implements TypeConverter {
    @Override
    public <T> T convertIfNecessary(Object o, Class<T> aClass) throws TypeMismatchException {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object o, Class<T> aClass, MethodParameter methodParameter) throws TypeMismatchException {
        return null;
    }

    @Override
    public <T> T convertIfNecessary(Object o, Class<T> aClass, Field field) throws TypeMismatchException {
        return null;
    }
}

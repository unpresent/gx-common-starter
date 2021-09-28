package ru.gx.common.updatable;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс для тестирования {@link UpdatableDefaultBeanFieldInfo}
 *
 * @author Adolin Negash 19.05.2021
 */
class BeanFieldInfoTest {

    @SuppressWarnings("unused")
    static class TestClass {

        private String data;
    }

    @Test
    void shouldSetValue() {
        final Field field = TestClass.class.getDeclaredFields()[0];
        final TestClass testClass = new TestClass();
        final UpdatableDefaultBeanFieldInfo info = new UpdatableDefaultBeanFieldInfo(testClass, "bean", field);
        final String someData = "some data";

        info.setValue(someData);
        assertEquals(someData, testClass.data);
    }
}

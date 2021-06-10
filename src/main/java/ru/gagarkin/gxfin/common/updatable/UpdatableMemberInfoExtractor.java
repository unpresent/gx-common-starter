package ru.gagarkin.gxfin.common.updatable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.tuple.Pair;
import ru.gagarkin.gxfin.common.annotations.UpdatableValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/**
 * Утилитный класс, вытаскивающий метаданные, необходимые для системы обновляемых полей.
 *
 * @author Adolin Negash 17.05.2021
 */
public class UpdatableMemberInfoExtractor {

    /**
     * Вытаскивает обновляемые поля.
     *
     * @param beanClass класс бина.
     * @return поток ({@link Stream}) пар поле-аннотация.
     */
    public Stream<Pair<Field, UpdatableValue>> extractUpdatableFields(Class<?> beanClass) {
        return FieldUtils.getAllFieldsList(beanClass).stream()
                .filter(UpdatableMemberInfoExtractor::isFieldValid)
                .map(field -> Pair.of(field, field.getAnnotation(UpdatableValue.class)))
                .filter(pair -> {
                    final UpdatableValue annotation = pair.getRight();
                    return annotation != null && StringUtils.isNoneBlank(annotation.value());
                });
    }

    /**
     * Вытаскивает обновляемые сеттеры.
     *
     * @param beanClass класс бина.
     * @return поток ({@link Stream}) пар метод-аннотация.
     */
    public Stream<Pair<Method, UpdatableValue>> extractUpdatableSetters(Class<?> beanClass) {
        return MethodUtils.getMethodsListWithAnnotation(beanClass, UpdatableValue.class, true, true)
                .stream()
                .filter(UpdatableMemberInfoExtractor::isSetterValid)
                .map(method -> Pair.of(method, method.getAnnotation(UpdatableValue.class)))
                .filter(pair -> {
                    final UpdatableValue annotation = pair.getRight();
                    return annotation != null && StringUtils.isNoneBlank(annotation.value());
                });
    }

    /**
     * Вытаскивает метод, вызываемый после обновления свойств.
     *
     * @param beanClass  класс бина.
     * @param methodName название метода бина.
     * @return метод бина.
     */
    public Method extractOnUpdateMethod(Class<?> beanClass, String methodName) {
        if (StringUtils.isBlank(methodName) || beanClass == null) {
            return null;
        }

        final Method method = MethodUtils.getMatchingMethod(beanClass, methodName);
        if (method != null) {
            final int modifiers = method.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                return method;
            }
        }

        return null;
    }

    private static boolean isFieldValid(Field field) {
        final Class<?> type = field.getType();
        final int modifiers = field.getModifiers();

        return type.isAssignableFrom(String.class)
                && !Modifier.isFinal(modifiers)
                && !Modifier.isStatic(modifiers);
    }

    private static boolean isSetterValid(Method method) {
        final Class<?>[] params = method.getParameterTypes();
        if (params.length != 1) {
            return false;
        }

        final int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            return false;
        }
        return params[0].isAssignableFrom(String.class);
    }
}

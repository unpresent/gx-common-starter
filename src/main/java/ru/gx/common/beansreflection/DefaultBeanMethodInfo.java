package ru.gx.common.beansreflection;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Информация о сеттере бина.
 */
public class DefaultBeanMethodInfo extends DefaultBeanMemberInfo {
    /**
     * Метод сеттера.
     */
    @Getter
    private final Method setter;

    /**
     * Создает объект с информацией о методах-сеттерах бина, используемых для обновляемых полей.
     *
     * @param bean     бин
     * @param beanName имя бина.
     * @param setter   метод-сеттер.
     */
    @SuppressWarnings("java:S3011")
    protected DefaultBeanMethodInfo(Object bean, String beanName, Method setter) {
        super(bean, beanName);
        this.setter = Objects.requireNonNull(setter);
        this.setter.setAccessible(true);
    }
}

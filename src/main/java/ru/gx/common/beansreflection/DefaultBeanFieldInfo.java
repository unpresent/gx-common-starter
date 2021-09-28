package ru.gx.common.beansreflection;

import lombok.Getter;

import java.lang.reflect.Field;

public class DefaultBeanFieldInfo extends DefaultBeanMemberInfo {
    /**
     * Поле в бине.
     */
    @Getter
    private final Field field;

    /**
     * Создает объект с информацией о поле бина.
     *
     * @param bean     бин.
     * @param beanName название бина.
     * @param field    поле.
     */
    protected DefaultBeanFieldInfo(Object bean, String beanName, Field field) {
        super(bean, beanName);
        this.field = field;
    }
}

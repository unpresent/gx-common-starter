package ru.gxfin.common.beansreflection;

import lombok.Getter;

/**
 * Базовый класс информации о бине.
 */
public class DefaultBeanMemberInfo implements BeanMemberInfo {
    /**
     * Бин.
     */
    @Getter
    protected final Object bean;

    /**
     * Имя бина.
     */
    @Getter
    private final String beanName;

    /**
     * @param bean     бин.
     * @param beanName имя бина.
     */
    protected DefaultBeanMemberInfo(Object bean, String beanName) {
        this.bean = bean;
        this.beanName = beanName;
    }
}

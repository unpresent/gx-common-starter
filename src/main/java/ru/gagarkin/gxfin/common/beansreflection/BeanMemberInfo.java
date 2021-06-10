package ru.gagarkin.gxfin.common.beansreflection;

/**
 * Базовый класс информации о бине.
 */
public interface BeanMemberInfo {
    /**
     * Бин, описатель которого данный объект представляет
     */
    Object getBean();

    /**
     * Имя бина.
     */
    String getBeanName();
}

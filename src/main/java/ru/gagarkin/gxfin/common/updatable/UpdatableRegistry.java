package ru.gagarkin.gxfin.common.updatable;

import ru.gagarkin.gxfin.common.annotations.Updatable;

import java.util.Collection;

/**
 * Реестр обновляемых свойств. Хранит свойства и обновляет их в привязанных бинах.
 *
 * @author Adolin Negash 13.05.2021
 */
public interface UpdatableRegistry {

    /**
     * Возвращает список свойств и их значений.
     *
     * @return список свойств.
     */
    Collection<UpdatablePropertyValue> getProperties();

    /**
     * Регистрирует в реестре бин с обновляемыми свойствами.
     *
     * @param beanName   имя бина.
     * @param bean       бин.
     * @param proxyBean  запроксированный бин.
     * @param annotation аннотация.
     */
    void registerBean(String beanName, Object bean, Object proxyBean, Updatable annotation);

    /**
     * Обновляет заданные свойства.
     *
     * @param listOfValues список обновляемых свойств и их значений.
     */
    void updateProperties(Collection<UpdatablePropertyValue> listOfValues) throws Exception;
}

package ru.gxfin.common.updatable;

import ru.gxfin.common.annotations.Updatable;

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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    void updateProperties(Collection<UpdatablePropertyValue> listOfValues) throws Exception;
}

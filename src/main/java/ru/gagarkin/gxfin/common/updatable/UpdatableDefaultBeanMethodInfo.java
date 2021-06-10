package ru.gagarkin.gxfin.common.updatable;

import lombok.extern.slf4j.Slf4j;
import ru.gagarkin.gxfin.common.beansreflection.DefaultBeanMethodInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Информация о сеттере бина.
 *
 * @author Adolin Negash 17.05.2021
 * @see Updatable
 */
@Slf4j
public class UpdatableDefaultBeanMethodInfo extends DefaultBeanMethodInfo implements UpdatableBeanMemberInfo {
    /**
     * Создает объект с информацией о методах-сеттерах бина, используемых для обновляемых полей.
     *
     * @param bean     бин
     * @param beanName имя бина.
     * @param setter   метод-сеттер.
     */
    UpdatableDefaultBeanMethodInfo(Object bean, String beanName, Method setter) {
        super(bean, beanName, setter);
    }

    /**
     * Передает новое значение в сеттер.
     *
     * @param value новое значение.
     */
    @Override
    public void setValue(String value) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Change value of type [{}], setter [{}]", bean.getClass(), this.getSetter().getName());
            }

            this.getSetter().invoke(bean, value);
        } catch (IllegalAccessException e) {
            log.error("Cannot update property - no access to setter {}.{}.", bean.getClass(), this.getSetter().getName(), e);
        } catch (InvocationTargetException e) {
            log.error("Cannot update property, internal error in setter {}.{}.", bean.getClass(), this.getSetter().getName(), e);
        }
    }
}

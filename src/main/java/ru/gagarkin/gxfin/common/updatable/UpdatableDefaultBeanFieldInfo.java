package ru.gagarkin.gxfin.common.updatable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import ru.gagarkin.gxfin.common.beansreflection.DefaultBeanFieldInfo;

import java.lang.reflect.Field;

/**
 * Информация о поле бина.
 *
 * @author Adolin Negash 13.05.2021
 */
@Slf4j
class UpdatableDefaultBeanFieldInfo extends DefaultBeanFieldInfo implements UpdatableBeanMemberInfo {

    /**
     * Создает объект с информацией о поле бина.
     *
     * @param bean     бин.
     * @param beanName название бина.
     * @param field    поле.
     */
    UpdatableDefaultBeanFieldInfo(Object bean, String beanName, Field field) {
        super(bean, beanName, field);
    }

    /**
     * Изменяет поле бина.
     *
     * @param value новое значение.
     */
    @Override
    public void setValue(String value) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Change value of type {}, field {}", bean.getClass(), this.getField().getName());
            }

            FieldUtils.writeField(this.getField(), bean, value, true);
        } catch (IllegalAccessException e) {
            log.error("Cannot update property no access to field {}.{}.", bean.getClass(), this.getField().getName(), e);
        }
    }
}

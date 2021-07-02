package ru.gxfin.common.updatable;

import ru.gxfin.common.beansreflection.BeanMemberInfo;

public interface UpdatableBeanMemberInfo extends BeanMemberInfo {
    /**
     * Изменяет значение свойства.
     *
     * @param value значение.
     */
    void setValue(String value);
}

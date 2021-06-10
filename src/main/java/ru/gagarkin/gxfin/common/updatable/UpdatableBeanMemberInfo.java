package ru.gagarkin.gxfin.common.updatable;

import ru.gagarkin.gxfin.common.beansreflection.BeanMemberInfo;

public interface UpdatableBeanMemberInfo extends BeanMemberInfo {
    /**
     * Изменяет значение свойства.
     *
     * @param value значение.
     */
    void setValue(String value);
}

package ru.gx.common.updatable;

import ru.gx.common.beansreflection.BeanMemberInfo;

public interface UpdatableBeanMemberInfo extends BeanMemberInfo {
    /**
     * Изменяет значение свойства.
     *
     * @param value значение.
     */
    void setValue(String value);
}

package ru.gagarkin.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация помечает поля или сеттеры, значения которых берутся из свойств и могут обновляться.
 *
 * @see UpdatableBean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface UpdatableValue {

    /**
     * Название свойства.
     */
    String value();
}

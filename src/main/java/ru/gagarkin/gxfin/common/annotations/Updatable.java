package ru.gagarkin.gxfin.common.annotations;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Аннотация помечает бины, которые содержат обновляемые свойства.
 *
 * @see UpdatableValue
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Bean
public @interface Updatable {

    /**
     * Список названий бина.
     *
     * @return список
     */
    @AliasFor(value = "value", annotation = Bean.class)
    String[] name() default {};

    /**
     * Метод, который будет вызван после обновления всех свойств в бине.
     */
    String onUpdateMethod() default "";
}

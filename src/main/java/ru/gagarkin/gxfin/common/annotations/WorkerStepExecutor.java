package ru.gagarkin.gxfin.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Орпделеям метод, который является обработчиком шага (итерацией) Worker-а
 * @see WorkerBean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface WorkerStepExecutor {
}

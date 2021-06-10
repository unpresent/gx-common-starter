package ru.gagarkin.gxfin.common.annotations;

import org.springframework.context.annotation.Bean;

import java.lang.annotation.*;

/**
 * Определяем бины, которые являются Worker-ами
 * @see WorkerStepExecutor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Bean
public @interface WorkerBean {
}

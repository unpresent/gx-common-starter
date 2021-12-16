package ru.gx.core.messaging;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@SuppressWarnings("unused")
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EventListener
@Documented
public @interface MessageListener {
    @AliasFor("classes")
    Class<? extends Message>[] value() default {};

    @AliasFor("value")
    Class<? extends Message>[] classes() default {};
}

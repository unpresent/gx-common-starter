package ru.gx.json;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
@JsonProperty
public @interface JsonPropertyEx {
    String USE_DEFAULT_NAME = "";
    int INDEX_UNKNOWN = -1;

    String value() default "";

    String namespace() default "";

    boolean required() default false;

    int index() default -1;

    String defaultValue() default "";

    com.fasterxml.jackson.annotation.JsonProperty.Access access() default com.fasterxml.jackson.annotation.JsonProperty.Access.AUTO;

    public static enum Access {
        AUTO,
        READ_ONLY,
        WRITE_ONLY,
        READ_WRITE;

        private Access() {
        }
    }
}

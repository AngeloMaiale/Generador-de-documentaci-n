package com.generator.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentedClass {
    String author() default "Desconocido";
    String description();
    String version() default "1.0";
}
package io.appform.hope.core.functions;

import io.appform.hope.core.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionImplementation {
    String value();
    Class<? extends Value>[] paramTypes() default {};
}

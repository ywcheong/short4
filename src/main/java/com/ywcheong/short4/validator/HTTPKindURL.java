package com.ywcheong.short4.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HTTPKindURLValidator.class)
public @interface HTTPKindURL {
    String message() default "HTTP 및 HTTPS 프로토콜만 허용합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

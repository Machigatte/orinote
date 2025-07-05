package com.chalkim.orinote.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = QuartzCronValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCron {
    String message() default "无效的 Quartz Cron 表达式";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

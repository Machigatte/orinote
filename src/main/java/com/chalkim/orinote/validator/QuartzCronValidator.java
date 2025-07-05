package com.chalkim.orinote.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.quartz.CronExpression;

public class QuartzCronValidator implements ConstraintValidator<ValidCron, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && CronExpression.isValidExpression(value);
    }
}
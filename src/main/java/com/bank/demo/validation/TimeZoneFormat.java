package com.bank.demo.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.ZoneId;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//@Target( { METHOD, FIELD, ANNOTATION_TYPE })
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidTimeZoneValidator.class)
public @interface TimeZoneFormat {
    String message() default "Invalid Time-zone ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

class ValidTimeZoneValidator
        implements ConstraintValidator<TimeZoneFormat, String> {

    @Override
    public void initialize(final TimeZoneFormat constraintAnnotation) {}

    @Override
    public boolean isValid(final String value,
                           final ConstraintValidatorContext context) {
        return value == null || ZoneId.getAvailableZoneIds().contains(value);
    }
}
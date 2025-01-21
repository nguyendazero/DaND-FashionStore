package com.haibazo_bff_its_rct_webapi.validation.annotation;

import com.haibazo_bff_its_rct_webapi.validation.validator.CanNotNegativeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CanNotNegativeValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CanNotNegative {
    String message() default "{validation.annotation.CanNotNegative.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
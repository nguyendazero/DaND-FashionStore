package com.haibazo_bff_its_rct_webapi.validation.annotation;

import com.haibazo_bff_its_rct_webapi.validation.validator.NoSpecialCharactersValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoSpecialCharactersValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSpecialCharacters {
    String message() default "{validation.annotation.NoSpecialCharacters.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
package com.haibazo_bff_its_rct_webapi.validation.validator;

import com.haibazo_bff_its_rct_webapi.validation.annotation.CanNotNegative;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CanNotNegativeValidator implements ConstraintValidator<CanNotNegative, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || value >= 0;
    }
}
package com.haibazo_bff_its_rct_webapi.validation.validator;

import com.haibazo_bff_its_rct_webapi.validation.annotation.NoSpecialCharacters;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpecialCharactersValidator implements ConstraintValidator<NoSpecialCharacters, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches("^[a-zA-Z0-9]*$");
    }
}
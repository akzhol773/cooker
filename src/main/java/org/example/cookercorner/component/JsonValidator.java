package org.example.cookercorner.component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.dtos.UserUpdateProfileDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JsonValidator {

    Validator validator;

    public void validateUserRequest(UserUpdateProfileDto request) {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "userUpdateProfileDto");
        validator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
        }
    }

    public void validateRecipeRequest(RecipeRequestDto request) {
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "recipeRequestDto");
        validator.validate(request, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
        }
    }
}

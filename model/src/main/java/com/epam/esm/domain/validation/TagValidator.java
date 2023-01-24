package com.epam.esm.domain.validation;

import com.epam.esm.domain.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagValidator extends EntityValidator {
    public boolean validate(Tag tag) {
        return tag != null
                && validateId(tag.getId())
                && validateName(tag.getName());
    }

    public boolean validateName(String name) {
        return validateField(name);
    }
}

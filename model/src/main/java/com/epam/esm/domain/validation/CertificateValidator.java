package com.epam.esm.domain.validation;

import com.epam.esm.domain.entity.Certificate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CertificateValidator extends EntityValidator {
    public boolean validate(Certificate certificate) {
        return validateId(certificate.getId())
                && validateName(certificate.getName())
                && validateDescription(certificate.getDescription())
                && validatePrice(certificate.getPrice())
                && validateDuration(certificate.getDuration());
    }

    public boolean validateName(String name) {
        return validateField(name);
    }

    public boolean validateDescription(String description) {
        return validateField(description);
    }

    public boolean validatePrice(BigDecimal price) {
        return price == null || price.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean validateDuration(Integer duration) {
        return validateId(duration);
    }
}

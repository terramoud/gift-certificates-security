package com.epam.esm.domain.converter;


import com.epam.esm.domain.dto.CertificateDto;
import com.epam.esm.domain.entity.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateDtoConverter {

    public Certificate ToCertificate(CertificateDto dto) {
        return new Certificate(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getDuration(),
                dto.getCreateDate(),
                dto.getLastUpdateDate(),
                dto.getTags()
        );
    }

    public CertificateDto toDto(Certificate certificate) {
        return new CertificateDto(
                certificate.getId(),
                certificate.getName(),
                certificate.getDescription(),
                certificate.getPrice(),
                certificate.getDuration(),
                certificate.getCreateDate(),
                certificate.getLastUpdateDate(),
                certificate.getTags()
        );
    }
}

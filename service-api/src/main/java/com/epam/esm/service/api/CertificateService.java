package com.epam.esm.service.api;

import com.epam.esm.domain.payload.CertificateDto;
import com.epam.esm.domain.payload.CertificateFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * The interface that provides methods for managing Gift Certificates.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
public interface CertificateService extends BaseService<CertificateDto, Long> {

    /**
     * Finds all Gift Certificates by given filter and page parameters.
     *
     * @param filter   the filter to be used to search for Gift Certificates
     * @param pageable the page parameters for the result list
     * @return a list of Gift Certificates that match the given
     *         filter and page parameters
     */
    List<CertificateDto> findAll(CertificateFilterDto filter, Pageable pageable);

    /**
     * Finds all Gift Certificates by a given tag ID,
     * filter and page parameters.
     *
     * @param tagId    the tag ID to be used to search for Gift Certificates
     * @param filter   the filter to be used to search for Gift Certificates
     * @param pageable the page parameters for the result list
     * @return a list of Gift Certificates that match the given tag ID, filter and page parameters
     */
    List<CertificateDto> findAllByTagId(Long tagId, CertificateFilterDto filter, Pageable pageable);

    /**
     * Finds all Gift Certificates by a given tag name, filter and page parameters.
     *
     * @param tagName  the tag name to be used to search for Gift Certificates
     * @param filter   the filter to be used to search for Gift Certificates
     * @param pageable the page parameters for the result list
     * @return a list of Gift Certificates that match the given tag name, filter and page parameters
     */
    List<CertificateDto> findAllByTagName(String tagName, CertificateFilterDto filter, Pageable pageable);
}

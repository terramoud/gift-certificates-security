package com.epam.esm.repository.api;

import com.epam.esm.domain.entity.Certificate;
import com.epam.esm.domain.payload.CertificateFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Certificate} entities.
 * Provides methods for retrieving certificates from the database.
 *
 * @author Oleksandr Koreshev
 * @since 1.0
 */
@Repository
public interface CertificateRepository extends BaseRepository<Certificate, Long> {

    /**
     * Finds all certificates matching the given filter and pageable criteria.
     *
     * @param filter the filter to apply when searching for certificates.
     * @param pageable the pageable criteria to apply when returning certificates.
     * @return a list of certificates matching the given criteria.
     */
    @Query(value = "SELECT c FROM Certificate c " +
            "WHERE " +
            "(:#{#filter.name} is null or c.name = :#{#filter.name}) AND " +
            "(:#{#filter.description} is null or c.description = :#{#filter.description}) AND " +
            "(:#{#filter.price} is null or c.price = :#{#filter.price}) AND " +
            "(:#{#filter.duration} is null or c.duration = :#{#filter.duration}) AND " +
            "(:#{#filter.createDate} is null or c.createDate = :#{#filter.createDate}) AND " +
            "(:#{#filter.lastUpdateDate} is null or c.lastUpdateDate = :#{#filter.lastUpdateDate}) AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#filter.nameContaining}, '%')) AND " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :#{#filter.descriptionContaining}, '%'))")
    List<Certificate> findAll(@Param("filter") CertificateFilterDto filter, Pageable pageable);

    /**
     * Finds all certificates associated with a given tag and matching
     * the given filter and pageable criteria.
     *
     * @param tagId the ID of the tag to search certificates for.
     * @param filter the filter to apply when searching for certificates.
     * @param pageable the pageable criteria to apply when returning certificates.
     * @return a list of certificates matching the given criteria.
     */
    @Query(value = "SELECT c FROM Certificate c JOIN c.tags t " +
            "WHERE t.id = :tagId AND " +
            "(:#{#filter.name} is null or c.name = :#{#filter.name}) AND " +
            "(:#{#filter.description} is null or c.description = :#{#filter.description}) AND " +
            "(:#{#filter.price} is null or c.price = :#{#filter.price}) AND " +
            "(:#{#filter.duration} is null or c.duration = :#{#filter.duration}) AND " +
            "(:#{#filter.createDate} is null or c.createDate = :#{#filter.createDate}) AND " +
            "(:#{#filter.lastUpdateDate} is null or c.lastUpdateDate = :#{#filter.lastUpdateDate}) AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#filter.nameContaining}, '%')) AND " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :#{#filter.descriptionContaining}, '%'))")
    List<Certificate> findAllByTagId(@Param("tagId") Long tagId,
                                     @Param("filter") CertificateFilterDto filter,
                                     Pageable pageable);

    /**
     * Finds all certificates associated with a given tag name and
     * matching the given filter and pageable criteria.
     *
     * @param tagName the name of the tag to search certificates for.
     * @param filter the filter to apply when searching for certificates.
     * @param pageable the pageable criteria to apply when returning certificates.
     * @return a list of certificates matching the given criteria.
     */
    @Query(value = "SELECT c FROM Certificate c JOIN c.tags t " +
            "WHERE t.name = :tagName AND " +
            "(:#{#filter.name} is null or c.name = :#{#filter.name}) AND " +
            "(:#{#filter.description} is null or c.description = :#{#filter.description}) AND " +
            "(:#{#filter.price} is null or c.price = :#{#filter.price}) AND " +
            "(:#{#filter.duration} is null or c.duration = :#{#filter.duration}) AND " +
            "(:#{#filter.createDate} is null or c.createDate = :#{#filter.createDate}) AND " +
            "(:#{#filter.lastUpdateDate} is null or c.lastUpdateDate = :#{#filter.lastUpdateDate}) AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#filter.nameContaining}, '%')) AND " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :#{#filter.descriptionContaining}, '%'))")
    List<Certificate> findAllByTagName(@Param("tagName") String tagName,
                                       @Param("filter") CertificateFilterDto filter,
                                       Pageable pageable);
}

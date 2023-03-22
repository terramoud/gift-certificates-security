package com.epam.esm.repository.api;



import com.epam.esm.domain.entity.Certificate;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends BaseRepository<Certificate, Long> {

}

package it.jhiptest.repository;

import it.jhiptest.domain.Gestore;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Gestore entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GestoreRepository extends JpaRepository<Gestore, Long> {}

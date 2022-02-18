package it.jhiptest.service;

import it.jhiptest.domain.Stato;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Stato}.
 */
public interface StatoService {
    /**
     * Save a stato.
     *
     * @param stato the entity to save.
     * @return the persisted entity.
     */
    Stato save(Stato stato);

    /**
     * Partially updates a stato.
     *
     * @param stato the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Stato> partialUpdate(Stato stato);

    /**
     * Get all the statoes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Stato> findAll(Pageable pageable);

    /**
     * Get the "id" stato.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Stato> findOne(Long id);

    /**
     * Delete the "id" stato.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

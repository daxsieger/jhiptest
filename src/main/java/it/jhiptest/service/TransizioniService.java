package it.jhiptest.service;

import it.jhiptest.domain.Transizioni;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Transizioni}.
 */
public interface TransizioniService {
    /**
     * Save a transizioni.
     *
     * @param transizioni the entity to save.
     * @return the persisted entity.
     */
    Transizioni save(Transizioni transizioni);

    /**
     * Partially updates a transizioni.
     *
     * @param transizioni the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Transizioni> partialUpdate(Transizioni transizioni);

    /**
     * Get all the transizionis.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Transizioni> findAll(Pageable pageable);

    /**
     * Get the "id" transizioni.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Transizioni> findOne(Long id);

    /**
     * Delete the "id" transizioni.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

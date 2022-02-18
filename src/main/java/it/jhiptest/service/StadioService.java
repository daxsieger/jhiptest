package it.jhiptest.service;

import it.jhiptest.domain.Stadio;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Stadio}.
 */
public interface StadioService {
    /**
     * Save a stadio.
     *
     * @param stadio the entity to save.
     * @return the persisted entity.
     */
    Stadio save(Stadio stadio);

    /**
     * Partially updates a stadio.
     *
     * @param stadio the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Stadio> partialUpdate(Stadio stadio);

    /**
     * Get all the stadios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Stadio> findAll(Pageable pageable);

    /**
     * Get the "id" stadio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Stadio> findOne(Long id);

    /**
     * Delete the "id" stadio.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

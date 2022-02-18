package it.jhiptest.service;

import it.jhiptest.domain.Assistito;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Assistito}.
 */
public interface AssistitoService {
    /**
     * Save a assistito.
     *
     * @param assistito the entity to save.
     * @return the persisted entity.
     */
    Assistito save(Assistito assistito);

    /**
     * Partially updates a assistito.
     *
     * @param assistito the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Assistito> partialUpdate(Assistito assistito);

    /**
     * Get all the assistitos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Assistito> findAll(Pageable pageable);

    /**
     * Get the "id" assistito.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Assistito> findOne(Long id);

    /**
     * Delete the "id" assistito.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

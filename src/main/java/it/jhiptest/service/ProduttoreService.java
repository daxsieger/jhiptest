package it.jhiptest.service;

import it.jhiptest.domain.Produttore;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Produttore}.
 */
public interface ProduttoreService {
    /**
     * Save a produttore.
     *
     * @param produttore the entity to save.
     * @return the persisted entity.
     */
    Produttore save(Produttore produttore);

    /**
     * Partially updates a produttore.
     *
     * @param produttore the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Produttore> partialUpdate(Produttore produttore);

    /**
     * Get all the produttores.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Produttore> findAll(Pageable pageable);

    /**
     * Get the "id" produttore.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Produttore> findOne(Long id);

    /**
     * Delete the "id" produttore.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

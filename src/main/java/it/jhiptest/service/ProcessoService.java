package it.jhiptest.service;

import it.jhiptest.domain.Processo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Processo}.
 */
public interface ProcessoService {
    /**
     * Save a processo.
     *
     * @param processo the entity to save.
     * @return the persisted entity.
     */
    Processo save(Processo processo);

    /**
     * Partially updates a processo.
     *
     * @param processo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Processo> partialUpdate(Processo processo);

    /**
     * Get all the processos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Processo> findAll(Pageable pageable);

    /**
     * Get the "id" processo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Processo> findOne(Long id);

    /**
     * Delete the "id" processo.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

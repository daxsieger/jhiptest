package it.jhiptest.service;

import it.jhiptest.domain.TipoEvento;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link TipoEvento}.
 */
public interface TipoEventoService {
    /**
     * Save a tipoEvento.
     *
     * @param tipoEvento the entity to save.
     * @return the persisted entity.
     */
    TipoEvento save(TipoEvento tipoEvento);

    /**
     * Partially updates a tipoEvento.
     *
     * @param tipoEvento the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TipoEvento> partialUpdate(TipoEvento tipoEvento);

    /**
     * Get all the tipoEventos.
     *
     * @return the list of entities.
     */
    List<TipoEvento> findAll();

    /**
     * Get the "id" tipoEvento.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TipoEvento> findOne(Long id);

    /**
     * Delete the "id" tipoEvento.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}

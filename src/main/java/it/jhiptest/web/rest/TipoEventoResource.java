package it.jhiptest.web.rest;

import it.jhiptest.domain.TipoEvento;
import it.jhiptest.repository.TipoEventoRepository;
import it.jhiptest.service.TipoEventoService;
import it.jhiptest.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.jhiptest.domain.TipoEvento}.
 */
@RestController
@RequestMapping("/api")
public class TipoEventoResource {

    private final Logger log = LoggerFactory.getLogger(TipoEventoResource.class);

    private static final String ENTITY_NAME = "jhiptestTipoEvento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TipoEventoService tipoEventoService;

    private final TipoEventoRepository tipoEventoRepository;

    public TipoEventoResource(TipoEventoService tipoEventoService, TipoEventoRepository tipoEventoRepository) {
        this.tipoEventoService = tipoEventoService;
        this.tipoEventoRepository = tipoEventoRepository;
    }

    /**
     * {@code POST  /tipo-eventos} : Create a new tipoEvento.
     *
     * @param tipoEvento the tipoEvento to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tipoEvento, or with status {@code 400 (Bad Request)} if the tipoEvento has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tipo-eventos")
    public ResponseEntity<TipoEvento> createTipoEvento(@RequestBody TipoEvento tipoEvento) throws URISyntaxException {
        log.debug("REST request to save TipoEvento : {}", tipoEvento);
        if (tipoEvento.getId() != null) {
            throw new BadRequestAlertException("A new tipoEvento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TipoEvento result = tipoEventoService.save(tipoEvento);
        return ResponseEntity
            .created(new URI("/api/tipo-eventos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tipo-eventos/:id} : Updates an existing tipoEvento.
     *
     * @param id the id of the tipoEvento to save.
     * @param tipoEvento the tipoEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoEvento,
     * or with status {@code 400 (Bad Request)} if the tipoEvento is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tipoEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tipo-eventos/{id}")
    public ResponseEntity<TipoEvento> updateTipoEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoEvento tipoEvento
    ) throws URISyntaxException {
        log.debug("REST request to update TipoEvento : {}, {}", id, tipoEvento);
        if (tipoEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoEventoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TipoEvento result = tipoEventoService.save(tipoEvento);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tipoEvento.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tipo-eventos/:id} : Partial updates given fields of an existing tipoEvento, field will ignore if it is null
     *
     * @param id the id of the tipoEvento to save.
     * @param tipoEvento the tipoEvento to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tipoEvento,
     * or with status {@code 400 (Bad Request)} if the tipoEvento is not valid,
     * or with status {@code 404 (Not Found)} if the tipoEvento is not found,
     * or with status {@code 500 (Internal Server Error)} if the tipoEvento couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tipo-eventos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TipoEvento> partialUpdateTipoEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TipoEvento tipoEvento
    ) throws URISyntaxException {
        log.debug("REST request to partial update TipoEvento partially : {}, {}", id, tipoEvento);
        if (tipoEvento.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tipoEvento.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tipoEventoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TipoEvento> result = tipoEventoService.partialUpdate(tipoEvento);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tipoEvento.getId().toString())
        );
    }

    /**
     * {@code GET  /tipo-eventos} : get all the tipoEventos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tipoEventos in body.
     */
    @GetMapping("/tipo-eventos")
    public List<TipoEvento> getAllTipoEventos() {
        log.debug("REST request to get all TipoEventos");
        return tipoEventoService.findAll();
    }

    /**
     * {@code GET  /tipo-eventos/:id} : get the "id" tipoEvento.
     *
     * @param id the id of the tipoEvento to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tipoEvento, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tipo-eventos/{id}")
    public ResponseEntity<TipoEvento> getTipoEvento(@PathVariable Long id) {
        log.debug("REST request to get TipoEvento : {}", id);
        Optional<TipoEvento> tipoEvento = tipoEventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tipoEvento);
    }

    /**
     * {@code DELETE  /tipo-eventos/:id} : delete the "id" tipoEvento.
     *
     * @param id the id of the tipoEvento to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tipo-eventos/{id}")
    public ResponseEntity<Void> deleteTipoEvento(@PathVariable Long id) {
        log.debug("REST request to delete TipoEvento : {}", id);
        tipoEventoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

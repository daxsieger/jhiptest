package it.jhiptest.web.rest;

import it.jhiptest.domain.Stato;
import it.jhiptest.repository.StatoRepository;
import it.jhiptest.service.StatoService;
import it.jhiptest.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.jhiptest.domain.Stato}.
 */
@RestController
@RequestMapping("/api")
public class StatoResource {

    private final Logger log = LoggerFactory.getLogger(StatoResource.class);

    private static final String ENTITY_NAME = "jhiptestStato";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StatoService statoService;

    private final StatoRepository statoRepository;

    public StatoResource(StatoService statoService, StatoRepository statoRepository) {
        this.statoService = statoService;
        this.statoRepository = statoRepository;
    }

    /**
     * {@code POST  /statoes} : Create a new stato.
     *
     * @param stato the stato to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stato, or with status {@code 400 (Bad Request)} if the stato has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/statoes")
    public ResponseEntity<Stato> createStato(@RequestBody Stato stato) throws URISyntaxException {
        log.debug("REST request to save Stato : {}", stato);
        if (stato.getId() != null) {
            throw new BadRequestAlertException("A new stato cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Stato result = statoService.save(stato);
        return ResponseEntity
            .created(new URI("/api/statoes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /statoes/:id} : Updates an existing stato.
     *
     * @param id the id of the stato to save.
     * @param stato the stato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stato,
     * or with status {@code 400 (Bad Request)} if the stato is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/statoes/{id}")
    public ResponseEntity<Stato> updateStato(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stato stato)
        throws URISyntaxException {
        log.debug("REST request to update Stato : {}, {}", id, stato);
        if (stato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Stato result = statoService.save(stato);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stato.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /statoes/:id} : Partial updates given fields of an existing stato, field will ignore if it is null
     *
     * @param id the id of the stato to save.
     * @param stato the stato to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stato,
     * or with status {@code 400 (Bad Request)} if the stato is not valid,
     * or with status {@code 404 (Not Found)} if the stato is not found,
     * or with status {@code 500 (Internal Server Error)} if the stato couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/statoes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Stato> partialUpdateStato(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stato stato)
        throws URISyntaxException {
        log.debug("REST request to partial update Stato partially : {}, {}", id, stato);
        if (stato.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stato.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!statoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stato> result = statoService.partialUpdate(stato);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stato.getId().toString())
        );
    }

    /**
     * {@code GET  /statoes} : get all the statoes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of statoes in body.
     */
    @GetMapping("/statoes")
    public ResponseEntity<List<Stato>> getAllStatoes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Statoes");
        Page<Stato> page = statoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /statoes/:id} : get the "id" stato.
     *
     * @param id the id of the stato to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stato, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/statoes/{id}")
    public ResponseEntity<Stato> getStato(@PathVariable Long id) {
        log.debug("REST request to get Stato : {}", id);
        Optional<Stato> stato = statoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stato);
    }

    /**
     * {@code DELETE  /statoes/:id} : delete the "id" stato.
     *
     * @param id the id of the stato to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/statoes/{id}")
    public ResponseEntity<Void> deleteStato(@PathVariable Long id) {
        log.debug("REST request to delete Stato : {}", id);
        statoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

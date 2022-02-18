package it.jhiptest.web.rest;

import it.jhiptest.domain.Gestore;
import it.jhiptest.repository.GestoreRepository;
import it.jhiptest.service.GestoreService;
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
 * REST controller for managing {@link it.jhiptest.domain.Gestore}.
 */
@RestController
@RequestMapping("/api")
public class GestoreResource {

    private final Logger log = LoggerFactory.getLogger(GestoreResource.class);

    private static final String ENTITY_NAME = "jhiptestGestore";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GestoreService gestoreService;

    private final GestoreRepository gestoreRepository;

    public GestoreResource(GestoreService gestoreService, GestoreRepository gestoreRepository) {
        this.gestoreService = gestoreService;
        this.gestoreRepository = gestoreRepository;
    }

    /**
     * {@code POST  /gestores} : Create a new gestore.
     *
     * @param gestore the gestore to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gestore, or with status {@code 400 (Bad Request)} if the gestore has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/gestores")
    public ResponseEntity<Gestore> createGestore(@RequestBody Gestore gestore) throws URISyntaxException {
        log.debug("REST request to save Gestore : {}", gestore);
        if (gestore.getId() != null) {
            throw new BadRequestAlertException("A new gestore cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Gestore result = gestoreService.save(gestore);
        return ResponseEntity
            .created(new URI("/api/gestores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /gestores/:id} : Updates an existing gestore.
     *
     * @param id the id of the gestore to save.
     * @param gestore the gestore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestore,
     * or with status {@code 400 (Bad Request)} if the gestore is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gestore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/gestores/{id}")
    public ResponseEntity<Gestore> updateGestore(@PathVariable(value = "id", required = false) final Long id, @RequestBody Gestore gestore)
        throws URISyntaxException {
        log.debug("REST request to update Gestore : {}, {}", id, gestore);
        if (gestore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gestoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Gestore result = gestoreService.save(gestore);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gestore.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /gestores/:id} : Partial updates given fields of an existing gestore, field will ignore if it is null
     *
     * @param id the id of the gestore to save.
     * @param gestore the gestore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestore,
     * or with status {@code 400 (Bad Request)} if the gestore is not valid,
     * or with status {@code 404 (Not Found)} if the gestore is not found,
     * or with status {@code 500 (Internal Server Error)} if the gestore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/gestores/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Gestore> partialUpdateGestore(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Gestore gestore
    ) throws URISyntaxException {
        log.debug("REST request to partial update Gestore partially : {}, {}", id, gestore);
        if (gestore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gestoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Gestore> result = gestoreService.partialUpdate(gestore);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gestore.getId().toString())
        );
    }

    /**
     * {@code GET  /gestores} : get all the gestores.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of gestores in body.
     */
    @GetMapping("/gestores")
    public ResponseEntity<List<Gestore>> getAllGestores(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Gestores");
        Page<Gestore> page = gestoreService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /gestores/:id} : get the "id" gestore.
     *
     * @param id the id of the gestore to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gestore, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/gestores/{id}")
    public ResponseEntity<Gestore> getGestore(@PathVariable Long id) {
        log.debug("REST request to get Gestore : {}", id);
        Optional<Gestore> gestore = gestoreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gestore);
    }

    /**
     * {@code DELETE  /gestores/:id} : delete the "id" gestore.
     *
     * @param id the id of the gestore to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/gestores/{id}")
    public ResponseEntity<Void> deleteGestore(@PathVariable Long id) {
        log.debug("REST request to delete Gestore : {}", id);
        gestoreService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

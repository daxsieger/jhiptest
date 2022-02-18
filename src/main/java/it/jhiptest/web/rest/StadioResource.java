package it.jhiptest.web.rest;

import it.jhiptest.domain.Stadio;
import it.jhiptest.repository.StadioRepository;
import it.jhiptest.service.StadioService;
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
 * REST controller for managing {@link it.jhiptest.domain.Stadio}.
 */
@RestController
@RequestMapping("/api")
public class StadioResource {

    private final Logger log = LoggerFactory.getLogger(StadioResource.class);

    private static final String ENTITY_NAME = "jhiptestStadio";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StadioService stadioService;

    private final StadioRepository stadioRepository;

    public StadioResource(StadioService stadioService, StadioRepository stadioRepository) {
        this.stadioService = stadioService;
        this.stadioRepository = stadioRepository;
    }

    /**
     * {@code POST  /stadios} : Create a new stadio.
     *
     * @param stadio the stadio to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stadio, or with status {@code 400 (Bad Request)} if the stadio has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stadios")
    public ResponseEntity<Stadio> createStadio(@RequestBody Stadio stadio) throws URISyntaxException {
        log.debug("REST request to save Stadio : {}", stadio);
        if (stadio.getId() != null) {
            throw new BadRequestAlertException("A new stadio cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Stadio result = stadioService.save(stadio);
        return ResponseEntity
            .created(new URI("/api/stadios/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stadios/:id} : Updates an existing stadio.
     *
     * @param id the id of the stadio to save.
     * @param stadio the stadio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stadio,
     * or with status {@code 400 (Bad Request)} if the stadio is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stadio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stadios/{id}")
    public ResponseEntity<Stadio> updateStadio(@PathVariable(value = "id", required = false) final Long id, @RequestBody Stadio stadio)
        throws URISyntaxException {
        log.debug("REST request to update Stadio : {}, {}", id, stadio);
        if (stadio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stadio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stadioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Stadio result = stadioService.save(stadio);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stadio.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stadios/:id} : Partial updates given fields of an existing stadio, field will ignore if it is null
     *
     * @param id the id of the stadio to save.
     * @param stadio the stadio to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stadio,
     * or with status {@code 400 (Bad Request)} if the stadio is not valid,
     * or with status {@code 404 (Not Found)} if the stadio is not found,
     * or with status {@code 500 (Internal Server Error)} if the stadio couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stadios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Stadio> partialUpdateStadio(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Stadio stadio
    ) throws URISyntaxException {
        log.debug("REST request to partial update Stadio partially : {}, {}", id, stadio);
        if (stadio.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stadio.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stadioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Stadio> result = stadioService.partialUpdate(stadio);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stadio.getId().toString())
        );
    }

    /**
     * {@code GET  /stadios} : get all the stadios.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stadios in body.
     */
    @GetMapping("/stadios")
    public ResponseEntity<List<Stadio>> getAllStadios(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Stadios");
        Page<Stadio> page = stadioService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stadios/:id} : get the "id" stadio.
     *
     * @param id the id of the stadio to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stadio, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stadios/{id}")
    public ResponseEntity<Stadio> getStadio(@PathVariable Long id) {
        log.debug("REST request to get Stadio : {}", id);
        Optional<Stadio> stadio = stadioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stadio);
    }

    /**
     * {@code DELETE  /stadios/:id} : delete the "id" stadio.
     *
     * @param id the id of the stadio to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stadios/{id}")
    public ResponseEntity<Void> deleteStadio(@PathVariable Long id) {
        log.debug("REST request to delete Stadio : {}", id);
        stadioService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

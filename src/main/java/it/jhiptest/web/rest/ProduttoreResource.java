package it.jhiptest.web.rest;

import it.jhiptest.domain.Produttore;
import it.jhiptest.repository.ProduttoreRepository;
import it.jhiptest.service.ProduttoreService;
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
 * REST controller for managing {@link it.jhiptest.domain.Produttore}.
 */
@RestController
@RequestMapping("/api")
public class ProduttoreResource {

    private final Logger log = LoggerFactory.getLogger(ProduttoreResource.class);

    private static final String ENTITY_NAME = "jhiptestProduttore";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProduttoreService produttoreService;

    private final ProduttoreRepository produttoreRepository;

    public ProduttoreResource(ProduttoreService produttoreService, ProduttoreRepository produttoreRepository) {
        this.produttoreService = produttoreService;
        this.produttoreRepository = produttoreRepository;
    }

    /**
     * {@code POST  /produttores} : Create a new produttore.
     *
     * @param produttore the produttore to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produttore, or with status {@code 400 (Bad Request)} if the produttore has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/produttores")
    public ResponseEntity<Produttore> createProduttore(@RequestBody Produttore produttore) throws URISyntaxException {
        log.debug("REST request to save Produttore : {}", produttore);
        if (produttore.getId() != null) {
            throw new BadRequestAlertException("A new produttore cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Produttore result = produttoreService.save(produttore);
        return ResponseEntity
            .created(new URI("/api/produttores/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /produttores/:id} : Updates an existing produttore.
     *
     * @param id the id of the produttore to save.
     * @param produttore the produttore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produttore,
     * or with status {@code 400 (Bad Request)} if the produttore is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produttore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/produttores/{id}")
    public ResponseEntity<Produttore> updateProduttore(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produttore produttore
    ) throws URISyntaxException {
        log.debug("REST request to update Produttore : {}, {}", id, produttore);
        if (produttore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produttore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produttoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Produttore result = produttoreService.save(produttore);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produttore.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /produttores/:id} : Partial updates given fields of an existing produttore, field will ignore if it is null
     *
     * @param id the id of the produttore to save.
     * @param produttore the produttore to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produttore,
     * or with status {@code 400 (Bad Request)} if the produttore is not valid,
     * or with status {@code 404 (Not Found)} if the produttore is not found,
     * or with status {@code 500 (Internal Server Error)} if the produttore couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/produttores/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Produttore> partialUpdateProduttore(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produttore produttore
    ) throws URISyntaxException {
        log.debug("REST request to partial update Produttore partially : {}, {}", id, produttore);
        if (produttore.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produttore.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!produttoreRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Produttore> result = produttoreService.partialUpdate(produttore);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, produttore.getId().toString())
        );
    }

    /**
     * {@code GET  /produttores} : get all the produttores.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produttores in body.
     */
    @GetMapping("/produttores")
    public ResponseEntity<List<Produttore>> getAllProduttores(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Produttores");
        Page<Produttore> page = produttoreService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /produttores/:id} : get the "id" produttore.
     *
     * @param id the id of the produttore to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produttore, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/produttores/{id}")
    public ResponseEntity<Produttore> getProduttore(@PathVariable Long id) {
        log.debug("REST request to get Produttore : {}", id);
        Optional<Produttore> produttore = produttoreService.findOne(id);
        return ResponseUtil.wrapOrNotFound(produttore);
    }

    /**
     * {@code DELETE  /produttores/:id} : delete the "id" produttore.
     *
     * @param id the id of the produttore to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/produttores/{id}")
    public ResponseEntity<Void> deleteProduttore(@PathVariable Long id) {
        log.debug("REST request to delete Produttore : {}", id);
        produttoreService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

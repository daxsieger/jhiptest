package it.jhiptest.web.rest;

import it.jhiptest.domain.Assistito;
import it.jhiptest.repository.AssistitoRepository;
import it.jhiptest.service.AssistitoService;
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
 * REST controller for managing {@link it.jhiptest.domain.Assistito}.
 */
@RestController
@RequestMapping("/api")
public class AssistitoResource {

    private final Logger log = LoggerFactory.getLogger(AssistitoResource.class);

    private static final String ENTITY_NAME = "jhiptestAssistito";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssistitoService assistitoService;

    private final AssistitoRepository assistitoRepository;

    public AssistitoResource(AssistitoService assistitoService, AssistitoRepository assistitoRepository) {
        this.assistitoService = assistitoService;
        this.assistitoRepository = assistitoRepository;
    }

    /**
     * {@code POST  /assistitos} : Create a new assistito.
     *
     * @param assistito the assistito to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assistito, or with status {@code 400 (Bad Request)} if the assistito has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/assistitos")
    public ResponseEntity<Assistito> createAssistito(@RequestBody Assistito assistito) throws URISyntaxException {
        log.debug("REST request to save Assistito : {}", assistito);
        if (assistito.getId() != null) {
            throw new BadRequestAlertException("A new assistito cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Assistito result = assistitoService.save(assistito);
        return ResponseEntity
            .created(new URI("/api/assistitos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /assistitos/:id} : Updates an existing assistito.
     *
     * @param id the id of the assistito to save.
     * @param assistito the assistito to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assistito,
     * or with status {@code 400 (Bad Request)} if the assistito is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assistito couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/assistitos/{id}")
    public ResponseEntity<Assistito> updateAssistito(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Assistito assistito
    ) throws URISyntaxException {
        log.debug("REST request to update Assistito : {}, {}", id, assistito);
        if (assistito.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assistito.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assistitoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Assistito result = assistitoService.save(assistito);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assistito.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /assistitos/:id} : Partial updates given fields of an existing assistito, field will ignore if it is null
     *
     * @param id the id of the assistito to save.
     * @param assistito the assistito to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assistito,
     * or with status {@code 400 (Bad Request)} if the assistito is not valid,
     * or with status {@code 404 (Not Found)} if the assistito is not found,
     * or with status {@code 500 (Internal Server Error)} if the assistito couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/assistitos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Assistito> partialUpdateAssistito(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Assistito assistito
    ) throws URISyntaxException {
        log.debug("REST request to partial update Assistito partially : {}, {}", id, assistito);
        if (assistito.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assistito.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assistitoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Assistito> result = assistitoService.partialUpdate(assistito);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assistito.getId().toString())
        );
    }

    /**
     * {@code GET  /assistitos} : get all the assistitos.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assistitos in body.
     */
    @GetMapping("/assistitos")
    public ResponseEntity<List<Assistito>> getAllAssistitos(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Assistitos");
        Page<Assistito> page = assistitoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assistitos/:id} : get the "id" assistito.
     *
     * @param id the id of the assistito to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assistito, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/assistitos/{id}")
    public ResponseEntity<Assistito> getAssistito(@PathVariable Long id) {
        log.debug("REST request to get Assistito : {}", id);
        Optional<Assistito> assistito = assistitoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assistito);
    }

    /**
     * {@code DELETE  /assistitos/:id} : delete the "id" assistito.
     *
     * @param id the id of the assistito to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/assistitos/{id}")
    public ResponseEntity<Void> deleteAssistito(@PathVariable Long id) {
        log.debug("REST request to delete Assistito : {}", id);
        assistitoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

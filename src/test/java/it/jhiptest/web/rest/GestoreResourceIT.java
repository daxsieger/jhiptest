package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Gestore;
import it.jhiptest.repository.GestoreRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GestoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GestoreResourceIT {

    private static final Long DEFAULT_ID_GESTORE = 1L;
    private static final Long UPDATED_ID_GESTORE = 2L;

    private static final String ENTITY_API_URL = "/api/gestores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GestoreRepository gestoreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGestoreMockMvc;

    private Gestore gestore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestore createEntity(EntityManager em) {
        Gestore gestore = new Gestore().idGestore(DEFAULT_ID_GESTORE);
        return gestore;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestore createUpdatedEntity(EntityManager em) {
        Gestore gestore = new Gestore().idGestore(UPDATED_ID_GESTORE);
        return gestore;
    }

    @BeforeEach
    public void initTest() {
        gestore = createEntity(em);
    }

    @Test
    @Transactional
    void createGestore() throws Exception {
        int databaseSizeBeforeCreate = gestoreRepository.findAll().size();
        // Create the Gestore
        restGestoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gestore)))
            .andExpect(status().isCreated());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeCreate + 1);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(DEFAULT_ID_GESTORE);
    }

    @Test
    @Transactional
    void createGestoreWithExistingId() throws Exception {
        // Create the Gestore with an existing ID
        gestore.setId(1L);

        int databaseSizeBeforeCreate = gestoreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGestoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gestore)))
            .andExpect(status().isBadRequest());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGestores() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        // Get all the gestoreList
        restGestoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gestore.getId().intValue())))
            .andExpect(jsonPath("$.[*].idGestore").value(hasItem(DEFAULT_ID_GESTORE.intValue())));
    }

    @Test
    @Transactional
    void getGestore() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        // Get the gestore
        restGestoreMockMvc
            .perform(get(ENTITY_API_URL_ID, gestore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gestore.getId().intValue()))
            .andExpect(jsonPath("$.idGestore").value(DEFAULT_ID_GESTORE.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingGestore() throws Exception {
        // Get the gestore
        restGestoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGestore() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();

        // Update the gestore
        Gestore updatedGestore = gestoreRepository.findById(gestore.getId()).get();
        // Disconnect from session so that the updates on updatedGestore are not directly saved in db
        em.detach(updatedGestore);
        updatedGestore.idGestore(UPDATED_ID_GESTORE);

        restGestoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGestore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGestore))
            )
            .andExpect(status().isOk());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    @Transactional
    void putNonExistingGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gestore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gestore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(gestore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(gestore)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGestoreWithPatch() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();

        // Update the gestore using partial update
        Gestore partialUpdatedGestore = new Gestore();
        partialUpdatedGestore.setId(gestore.getId());

        partialUpdatedGestore.idGestore(UPDATED_ID_GESTORE);

        restGestoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGestore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGestore))
            )
            .andExpect(status().isOk());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    @Transactional
    void fullUpdateGestoreWithPatch() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();

        // Update the gestore using partial update
        Gestore partialUpdatedGestore = new Gestore();
        partialUpdatedGestore.setId(gestore.getId());

        partialUpdatedGestore.idGestore(UPDATED_ID_GESTORE);

        restGestoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGestore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGestore))
            )
            .andExpect(status().isOk());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    @Transactional
    void patchNonExistingGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gestore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gestore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(gestore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestoreMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(gestore)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGestore() throws Exception {
        // Initialize the database
        gestoreRepository.saveAndFlush(gestore);

        int databaseSizeBeforeDelete = gestoreRepository.findAll().size();

        // Delete the gestore
        restGestoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, gestore.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Gestore> gestoreList = gestoreRepository.findAll();
        assertThat(gestoreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

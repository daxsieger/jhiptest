package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Stadio;
import it.jhiptest.repository.StadioRepository;
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
 * Integration tests for the {@link StadioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StadioResourceIT {

    private static final Long DEFAULT_ID_STADIO = 1L;
    private static final Long UPDATED_ID_STADIO = 2L;

    private static final String DEFAULT_DS_STADIO = "AAAAAAAAAA";
    private static final String UPDATED_DS_STADIO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stadios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StadioRepository stadioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStadioMockMvc;

    private Stadio stadio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stadio createEntity(EntityManager em) {
        Stadio stadio = new Stadio().idStadio(DEFAULT_ID_STADIO).dsStadio(DEFAULT_DS_STADIO);
        return stadio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stadio createUpdatedEntity(EntityManager em) {
        Stadio stadio = new Stadio().idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);
        return stadio;
    }

    @BeforeEach
    public void initTest() {
        stadio = createEntity(em);
    }

    @Test
    @Transactional
    void createStadio() throws Exception {
        int databaseSizeBeforeCreate = stadioRepository.findAll().size();
        // Create the Stadio
        restStadioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stadio)))
            .andExpect(status().isCreated());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeCreate + 1);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(DEFAULT_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
    }

    @Test
    @Transactional
    void createStadioWithExistingId() throws Exception {
        // Create the Stadio with an existing ID
        stadio.setId(1L);

        int databaseSizeBeforeCreate = stadioRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStadioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stadio)))
            .andExpect(status().isBadRequest());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStadios() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        // Get all the stadioList
        restStadioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stadio.getId().intValue())))
            .andExpect(jsonPath("$.[*].idStadio").value(hasItem(DEFAULT_ID_STADIO.intValue())))
            .andExpect(jsonPath("$.[*].dsStadio").value(hasItem(DEFAULT_DS_STADIO)));
    }

    @Test
    @Transactional
    void getStadio() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        // Get the stadio
        restStadioMockMvc
            .perform(get(ENTITY_API_URL_ID, stadio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stadio.getId().intValue()))
            .andExpect(jsonPath("$.idStadio").value(DEFAULT_ID_STADIO.intValue()))
            .andExpect(jsonPath("$.dsStadio").value(DEFAULT_DS_STADIO));
    }

    @Test
    @Transactional
    void getNonExistingStadio() throws Exception {
        // Get the stadio
        restStadioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStadio() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();

        // Update the stadio
        Stadio updatedStadio = stadioRepository.findById(stadio.getId()).get();
        // Disconnect from session so that the updates on updatedStadio are not directly saved in db
        em.detach(updatedStadio);
        updatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        restStadioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStadio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStadio))
            )
            .andExpect(status().isOk());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    @Transactional
    void putNonExistingStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stadio.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stadio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stadio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stadio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStadioWithPatch() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();

        // Update the stadio using partial update
        Stadio partialUpdatedStadio = new Stadio();
        partialUpdatedStadio.setId(stadio.getId());

        partialUpdatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        restStadioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStadio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStadio))
            )
            .andExpect(status().isOk());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    @Transactional
    void fullUpdateStadioWithPatch() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();

        // Update the stadio using partial update
        Stadio partialUpdatedStadio = new Stadio();
        partialUpdatedStadio.setId(stadio.getId());

        partialUpdatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        restStadioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStadio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStadio))
            )
            .andExpect(status().isOk());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    @Transactional
    void patchNonExistingStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stadio.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stadio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stadio))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStadioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stadio)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStadio() throws Exception {
        // Initialize the database
        stadioRepository.saveAndFlush(stadio);

        int databaseSizeBeforeDelete = stadioRepository.findAll().size();

        // Delete the stadio
        restStadioMockMvc
            .perform(delete(ENTITY_API_URL_ID, stadio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stadio> stadioList = stadioRepository.findAll();
        assertThat(stadioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

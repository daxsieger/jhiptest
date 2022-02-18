package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Stato;
import it.jhiptest.repository.StatoRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link StatoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StatoResourceIT {

    private static final Long DEFAULT_ID_STADIO = 1L;
    private static final Long UPDATED_ID_STADIO = 2L;

    private static final String DEFAULT_DS_STADIO = "AAAAAAAAAA";
    private static final String UPDATED_DS_STADIO = "BBBBBBBBBB";

    private static final Instant DEFAULT_TS_CAMBIO_STATO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TS_CAMBIO_STATO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/statoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatoRepository statoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStatoMockMvc;

    private Stato stato;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stato createEntity(EntityManager em) {
        Stato stato = new Stato().idStadio(DEFAULT_ID_STADIO).dsStadio(DEFAULT_DS_STADIO).tsCambioStato(DEFAULT_TS_CAMBIO_STATO);
        return stato;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stato createUpdatedEntity(EntityManager em) {
        Stato stato = new Stato().idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);
        return stato;
    }

    @BeforeEach
    public void initTest() {
        stato = createEntity(em);
    }

    @Test
    @Transactional
    void createStato() throws Exception {
        int databaseSizeBeforeCreate = statoRepository.findAll().size();
        // Create the Stato
        restStatoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stato)))
            .andExpect(status().isCreated());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeCreate + 1);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(DEFAULT_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(DEFAULT_TS_CAMBIO_STATO);
    }

    @Test
    @Transactional
    void createStatoWithExistingId() throws Exception {
        // Create the Stato with an existing ID
        stato.setId(1L);

        int databaseSizeBeforeCreate = statoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStatoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stato)))
            .andExpect(status().isBadRequest());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStatoes() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        // Get all the statoList
        restStatoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stato.getId().intValue())))
            .andExpect(jsonPath("$.[*].idStadio").value(hasItem(DEFAULT_ID_STADIO.intValue())))
            .andExpect(jsonPath("$.[*].dsStadio").value(hasItem(DEFAULT_DS_STADIO)))
            .andExpect(jsonPath("$.[*].tsCambioStato").value(hasItem(DEFAULT_TS_CAMBIO_STATO.toString())));
    }

    @Test
    @Transactional
    void getStato() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        // Get the stato
        restStatoMockMvc
            .perform(get(ENTITY_API_URL_ID, stato.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stato.getId().intValue()))
            .andExpect(jsonPath("$.idStadio").value(DEFAULT_ID_STADIO.intValue()))
            .andExpect(jsonPath("$.dsStadio").value(DEFAULT_DS_STADIO))
            .andExpect(jsonPath("$.tsCambioStato").value(DEFAULT_TS_CAMBIO_STATO.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStato() throws Exception {
        // Get the stato
        restStatoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewStato() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        int databaseSizeBeforeUpdate = statoRepository.findAll().size();

        // Update the stato
        Stato updatedStato = statoRepository.findById(stato.getId()).get();
        // Disconnect from session so that the updates on updatedStato are not directly saved in db
        em.detach(updatedStato);
        updatedStato.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        restStatoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStato.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStato))
            )
            .andExpect(status().isOk());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    @Transactional
    void putNonExistingStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stato.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(stato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(stato)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStatoWithPatch() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        int databaseSizeBeforeUpdate = statoRepository.findAll().size();

        // Update the stato using partial update
        Stato partialUpdatedStato = new Stato();
        partialUpdatedStato.setId(stato.getId());

        partialUpdatedStato.idStadio(UPDATED_ID_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        restStatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStato.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStato))
            )
            .andExpect(status().isOk());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    @Transactional
    void fullUpdateStatoWithPatch() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        int databaseSizeBeforeUpdate = statoRepository.findAll().size();

        // Update the stato using partial update
        Stato partialUpdatedStato = new Stato();
        partialUpdatedStato.setId(stato.getId());

        partialUpdatedStato.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        restStatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStato.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStato))
            )
            .andExpect(status().isOk());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    @Transactional
    void patchNonExistingStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stato.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(stato))
            )
            .andExpect(status().isBadRequest());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStatoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(stato)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStato() throws Exception {
        // Initialize the database
        statoRepository.saveAndFlush(stato);

        int databaseSizeBeforeDelete = statoRepository.findAll().size();

        // Delete the stato
        restStatoMockMvc
            .perform(delete(ENTITY_API_URL_ID, stato.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Stato> statoList = statoRepository.findAll();
        assertThat(statoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

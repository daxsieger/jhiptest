package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Transizioni;
import it.jhiptest.repository.TransizioniRepository;
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
 * Integration tests for the {@link TransizioniResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransizioniResourceIT {

    private static final Long DEFAULT_ID_TRANSIZIONE = 1L;
    private static final Long UPDATED_ID_TRANSIZIONE = 2L;

    private static final String DEFAULT_DS_TRANSIZIONE = "AAAAAAAAAA";
    private static final String UPDATED_DS_TRANSIZIONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transizionis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransizioniRepository transizioniRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransizioniMockMvc;

    private Transizioni transizioni;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transizioni createEntity(EntityManager em) {
        Transizioni transizioni = new Transizioni().idTransizione(DEFAULT_ID_TRANSIZIONE).dsTransizione(DEFAULT_DS_TRANSIZIONE);
        return transizioni;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transizioni createUpdatedEntity(EntityManager em) {
        Transizioni transizioni = new Transizioni().idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);
        return transizioni;
    }

    @BeforeEach
    public void initTest() {
        transizioni = createEntity(em);
    }

    @Test
    @Transactional
    void createTransizioni() throws Exception {
        int databaseSizeBeforeCreate = transizioniRepository.findAll().size();
        // Create the Transizioni
        restTransizioniMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transizioni)))
            .andExpect(status().isCreated());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeCreate + 1);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(DEFAULT_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(DEFAULT_DS_TRANSIZIONE);
    }

    @Test
    @Transactional
    void createTransizioniWithExistingId() throws Exception {
        // Create the Transizioni with an existing ID
        transizioni.setId(1L);

        int databaseSizeBeforeCreate = transizioniRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransizioniMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transizioni)))
            .andExpect(status().isBadRequest());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTransizionis() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        // Get all the transizioniList
        restTransizioniMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transizioni.getId().intValue())))
            .andExpect(jsonPath("$.[*].idTransizione").value(hasItem(DEFAULT_ID_TRANSIZIONE.intValue())))
            .andExpect(jsonPath("$.[*].dsTransizione").value(hasItem(DEFAULT_DS_TRANSIZIONE)));
    }

    @Test
    @Transactional
    void getTransizioni() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        // Get the transizioni
        restTransizioniMockMvc
            .perform(get(ENTITY_API_URL_ID, transizioni.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transizioni.getId().intValue()))
            .andExpect(jsonPath("$.idTransizione").value(DEFAULT_ID_TRANSIZIONE.intValue()))
            .andExpect(jsonPath("$.dsTransizione").value(DEFAULT_DS_TRANSIZIONE));
    }

    @Test
    @Transactional
    void getNonExistingTransizioni() throws Exception {
        // Get the transizioni
        restTransizioniMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransizioni() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();

        // Update the transizioni
        Transizioni updatedTransizioni = transizioniRepository.findById(transizioni.getId()).get();
        // Disconnect from session so that the updates on updatedTransizioni are not directly saved in db
        em.detach(updatedTransizioni);
        updatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);

        restTransizioniMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransizioni.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransizioni))
            )
            .andExpect(status().isOk());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(UPDATED_DS_TRANSIZIONE);
    }

    @Test
    @Transactional
    void putNonExistingTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transizioni.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transizioni))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transizioni))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transizioni)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransizioniWithPatch() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();

        // Update the transizioni using partial update
        Transizioni partialUpdatedTransizioni = new Transizioni();
        partialUpdatedTransizioni.setId(transizioni.getId());

        partialUpdatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE);

        restTransizioniMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransizioni.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransizioni))
            )
            .andExpect(status().isOk());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(DEFAULT_DS_TRANSIZIONE);
    }

    @Test
    @Transactional
    void fullUpdateTransizioniWithPatch() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();

        // Update the transizioni using partial update
        Transizioni partialUpdatedTransizioni = new Transizioni();
        partialUpdatedTransizioni.setId(transizioni.getId());

        partialUpdatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);

        restTransizioniMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransizioni.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransizioni))
            )
            .andExpect(status().isOk());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(UPDATED_DS_TRANSIZIONE);
    }

    @Test
    @Transactional
    void patchNonExistingTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transizioni.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transizioni))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transizioni))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransizioniMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transizioni))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransizioni() throws Exception {
        // Initialize the database
        transizioniRepository.saveAndFlush(transizioni);

        int databaseSizeBeforeDelete = transizioniRepository.findAll().size();

        // Delete the transizioni
        restTransizioniMockMvc
            .perform(delete(ENTITY_API_URL_ID, transizioni.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transizioni> transizioniList = transizioniRepository.findAll();
        assertThat(transizioniList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

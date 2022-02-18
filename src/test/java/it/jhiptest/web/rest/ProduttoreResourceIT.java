package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Produttore;
import it.jhiptest.repository.ProduttoreRepository;
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
 * Integration tests for the {@link ProduttoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProduttoreResourceIT {

    private static final Long DEFAULT_ID_PRODUTTORE = 1L;
    private static final Long UPDATED_ID_PRODUTTORE = 2L;

    private static final String DEFAULT_DS_PRODUTTORE = "AAAAAAAAAA";
    private static final String UPDATED_DS_PRODUTTORE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/produttores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProduttoreRepository produttoreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProduttoreMockMvc;

    private Produttore produttore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produttore createEntity(EntityManager em) {
        Produttore produttore = new Produttore().idProduttore(DEFAULT_ID_PRODUTTORE).dsProduttore(DEFAULT_DS_PRODUTTORE);
        return produttore;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produttore createUpdatedEntity(EntityManager em) {
        Produttore produttore = new Produttore().idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);
        return produttore;
    }

    @BeforeEach
    public void initTest() {
        produttore = createEntity(em);
    }

    @Test
    @Transactional
    void createProduttore() throws Exception {
        int databaseSizeBeforeCreate = produttoreRepository.findAll().size();
        // Create the Produttore
        restProduttoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produttore)))
            .andExpect(status().isCreated());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeCreate + 1);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(DEFAULT_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(DEFAULT_DS_PRODUTTORE);
    }

    @Test
    @Transactional
    void createProduttoreWithExistingId() throws Exception {
        // Create the Produttore with an existing ID
        produttore.setId(1L);

        int databaseSizeBeforeCreate = produttoreRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduttoreMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produttore)))
            .andExpect(status().isBadRequest());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProduttores() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        // Get all the produttoreList
        restProduttoreMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produttore.getId().intValue())))
            .andExpect(jsonPath("$.[*].idProduttore").value(hasItem(DEFAULT_ID_PRODUTTORE.intValue())))
            .andExpect(jsonPath("$.[*].dsProduttore").value(hasItem(DEFAULT_DS_PRODUTTORE)));
    }

    @Test
    @Transactional
    void getProduttore() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        // Get the produttore
        restProduttoreMockMvc
            .perform(get(ENTITY_API_URL_ID, produttore.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produttore.getId().intValue()))
            .andExpect(jsonPath("$.idProduttore").value(DEFAULT_ID_PRODUTTORE.intValue()))
            .andExpect(jsonPath("$.dsProduttore").value(DEFAULT_DS_PRODUTTORE));
    }

    @Test
    @Transactional
    void getNonExistingProduttore() throws Exception {
        // Get the produttore
        restProduttoreMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewProduttore() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();

        // Update the produttore
        Produttore updatedProduttore = produttoreRepository.findById(produttore.getId()).get();
        // Disconnect from session so that the updates on updatedProduttore are not directly saved in db
        em.detach(updatedProduttore);
        updatedProduttore.idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);

        restProduttoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedProduttore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedProduttore))
            )
            .andExpect(status().isOk());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(UPDATED_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    @Transactional
    void putNonExistingProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produttore.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produttore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(produttore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(produttore)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProduttoreWithPatch() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();

        // Update the produttore using partial update
        Produttore partialUpdatedProduttore = new Produttore();
        partialUpdatedProduttore.setId(produttore.getId());

        partialUpdatedProduttore.dsProduttore(UPDATED_DS_PRODUTTORE);

        restProduttoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduttore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduttore))
            )
            .andExpect(status().isOk());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(DEFAULT_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    @Transactional
    void fullUpdateProduttoreWithPatch() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();

        // Update the produttore using partial update
        Produttore partialUpdatedProduttore = new Produttore();
        partialUpdatedProduttore.setId(produttore.getId());

        partialUpdatedProduttore.idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);

        restProduttoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduttore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedProduttore))
            )
            .andExpect(status().isOk());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(UPDATED_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    @Transactional
    void patchNonExistingProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produttore.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produttore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(produttore))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduttoreMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(produttore))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduttore() throws Exception {
        // Initialize the database
        produttoreRepository.saveAndFlush(produttore);

        int databaseSizeBeforeDelete = produttoreRepository.findAll().size();

        // Delete the produttore
        restProduttoreMockMvc
            .perform(delete(ENTITY_API_URL_ID, produttore.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Produttore> produttoreList = produttoreRepository.findAll();
        assertThat(produttoreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

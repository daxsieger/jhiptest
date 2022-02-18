package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.Assistito;
import it.jhiptest.repository.AssistitoRepository;
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
 * Integration tests for the {@link AssistitoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssistitoResourceIT {

    private static final Long DEFAULT_ID_ASSISTITO = 1L;
    private static final Long UPDATED_ID_ASSISTITO = 2L;

    private static final String ENTITY_API_URL = "/api/assistitos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AssistitoRepository assistitoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssistitoMockMvc;

    private Assistito assistito;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assistito createEntity(EntityManager em) {
        Assistito assistito = new Assistito().idAssistito(DEFAULT_ID_ASSISTITO);
        return assistito;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assistito createUpdatedEntity(EntityManager em) {
        Assistito assistito = new Assistito().idAssistito(UPDATED_ID_ASSISTITO);
        return assistito;
    }

    @BeforeEach
    public void initTest() {
        assistito = createEntity(em);
    }

    @Test
    @Transactional
    void createAssistito() throws Exception {
        int databaseSizeBeforeCreate = assistitoRepository.findAll().size();
        // Create the Assistito
        restAssistitoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(assistito)))
            .andExpect(status().isCreated());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeCreate + 1);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(DEFAULT_ID_ASSISTITO);
    }

    @Test
    @Transactional
    void createAssistitoWithExistingId() throws Exception {
        // Create the Assistito with an existing ID
        assistito.setId(1L);

        int databaseSizeBeforeCreate = assistitoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssistitoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(assistito)))
            .andExpect(status().isBadRequest());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAssistitos() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        // Get all the assistitoList
        restAssistitoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assistito.getId().intValue())))
            .andExpect(jsonPath("$.[*].idAssistito").value(hasItem(DEFAULT_ID_ASSISTITO.intValue())));
    }

    @Test
    @Transactional
    void getAssistito() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        // Get the assistito
        restAssistitoMockMvc
            .perform(get(ENTITY_API_URL_ID, assistito.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assistito.getId().intValue()))
            .andExpect(jsonPath("$.idAssistito").value(DEFAULT_ID_ASSISTITO.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingAssistito() throws Exception {
        // Get the assistito
        restAssistitoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAssistito() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();

        // Update the assistito
        Assistito updatedAssistito = assistitoRepository.findById(assistito.getId()).get();
        // Disconnect from session so that the updates on updatedAssistito are not directly saved in db
        em.detach(updatedAssistito);
        updatedAssistito.idAssistito(UPDATED_ID_ASSISTITO);

        restAssistitoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAssistito.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAssistito))
            )
            .andExpect(status().isOk());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(UPDATED_ID_ASSISTITO);
    }

    @Test
    @Transactional
    void putNonExistingAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assistito.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assistito))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(assistito))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(assistito)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssistitoWithPatch() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();

        // Update the assistito using partial update
        Assistito partialUpdatedAssistito = new Assistito();
        partialUpdatedAssistito.setId(assistito.getId());

        restAssistitoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssistito.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssistito))
            )
            .andExpect(status().isOk());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(DEFAULT_ID_ASSISTITO);
    }

    @Test
    @Transactional
    void fullUpdateAssistitoWithPatch() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();

        // Update the assistito using partial update
        Assistito partialUpdatedAssistito = new Assistito();
        partialUpdatedAssistito.setId(assistito.getId());

        partialUpdatedAssistito.idAssistito(UPDATED_ID_ASSISTITO);

        restAssistitoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssistito.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAssistito))
            )
            .andExpect(status().isOk());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(UPDATED_ID_ASSISTITO);
    }

    @Test
    @Transactional
    void patchNonExistingAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assistito.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assistito))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(assistito))
            )
            .andExpect(status().isBadRequest());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssistitoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(assistito))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssistito() throws Exception {
        // Initialize the database
        assistitoRepository.saveAndFlush(assistito);

        int databaseSizeBeforeDelete = assistitoRepository.findAll().size();

        // Delete the assistito
        restAssistitoMockMvc
            .perform(delete(ENTITY_API_URL_ID, assistito.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Assistito> assistitoList = assistitoRepository.findAll();
        assertThat(assistitoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

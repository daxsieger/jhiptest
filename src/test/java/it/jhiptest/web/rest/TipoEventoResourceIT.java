package it.jhiptest.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.jhiptest.IntegrationTest;
import it.jhiptest.domain.TipoEvento;
import it.jhiptest.repository.TipoEventoRepository;
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
 * Integration tests for the {@link TipoEventoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TipoEventoResourceIT {

    private static final Long DEFAULT_ID_TIPO_EVENTO = 1L;
    private static final Long UPDATED_ID_TIPO_EVENTO = 2L;

    private static final String DEFAULT_DS_TIPO_EVENTO = "AAAAAAAAAA";
    private static final String UPDATED_DS_TIPO_EVENTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tipo-eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTipoEventoMockMvc;

    private TipoEvento tipoEvento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoEvento createEntity(EntityManager em) {
        TipoEvento tipoEvento = new TipoEvento().idTipoEvento(DEFAULT_ID_TIPO_EVENTO).dsTipoEvento(DEFAULT_DS_TIPO_EVENTO);
        return tipoEvento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoEvento createUpdatedEntity(EntityManager em) {
        TipoEvento tipoEvento = new TipoEvento().idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);
        return tipoEvento;
    }

    @BeforeEach
    public void initTest() {
        tipoEvento = createEntity(em);
    }

    @Test
    @Transactional
    void createTipoEvento() throws Exception {
        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().size();
        // Create the TipoEvento
        restTipoEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tipoEvento)))
            .andExpect(status().isCreated());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate + 1);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(DEFAULT_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(DEFAULT_DS_TIPO_EVENTO);
    }

    @Test
    @Transactional
    void createTipoEventoWithExistingId() throws Exception {
        // Create the TipoEvento with an existing ID
        tipoEvento.setId(1L);

        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipoEventoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tipoEvento)))
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTipoEventos() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        // Get all the tipoEventoList
        restTipoEventoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoEvento.getId().intValue())))
            .andExpect(jsonPath("$.[*].idTipoEvento").value(hasItem(DEFAULT_ID_TIPO_EVENTO.intValue())))
            .andExpect(jsonPath("$.[*].dsTipoEvento").value(hasItem(DEFAULT_DS_TIPO_EVENTO)));
    }

    @Test
    @Transactional
    void getTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        // Get the tipoEvento
        restTipoEventoMockMvc
            .perform(get(ENTITY_API_URL_ID, tipoEvento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tipoEvento.getId().intValue()))
            .andExpect(jsonPath("$.idTipoEvento").value(DEFAULT_ID_TIPO_EVENTO.intValue()))
            .andExpect(jsonPath("$.dsTipoEvento").value(DEFAULT_DS_TIPO_EVENTO));
    }

    @Test
    @Transactional
    void getNonExistingTipoEvento() throws Exception {
        // Get the tipoEvento
        restTipoEventoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();

        // Update the tipoEvento
        TipoEvento updatedTipoEvento = tipoEventoRepository.findById(tipoEvento.getId()).get();
        // Disconnect from session so that the updates on updatedTipoEvento are not directly saved in db
        em.detach(updatedTipoEvento);
        updatedTipoEvento.idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);

        restTipoEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTipoEvento.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTipoEvento))
            )
            .andExpect(status().isOk());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(UPDATED_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(UPDATED_DS_TIPO_EVENTO);
    }

    @Test
    @Transactional
    void putNonExistingTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tipoEvento.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tipoEvento))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tipoEvento))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tipoEvento)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTipoEventoWithPatch() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();

        // Update the tipoEvento using partial update
        TipoEvento partialUpdatedTipoEvento = new TipoEvento();
        partialUpdatedTipoEvento.setId(tipoEvento.getId());

        restTipoEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipoEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoEvento))
            )
            .andExpect(status().isOk());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(DEFAULT_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(DEFAULT_DS_TIPO_EVENTO);
    }

    @Test
    @Transactional
    void fullUpdateTipoEventoWithPatch() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();

        // Update the tipoEvento using partial update
        TipoEvento partialUpdatedTipoEvento = new TipoEvento();
        partialUpdatedTipoEvento.setId(tipoEvento.getId());

        partialUpdatedTipoEvento.idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);

        restTipoEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTipoEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoEvento))
            )
            .andExpect(status().isOk());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(UPDATED_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(UPDATED_DS_TIPO_EVENTO);
    }

    @Test
    @Transactional
    void patchNonExistingTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tipoEvento.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tipoEvento))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tipoEvento))
            )
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tipoEvento))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeDelete = tipoEventoRepository.findAll().size();

        // Delete the tipoEvento
        restTipoEventoMockMvc
            .perform(delete(ENTITY_API_URL_ID, tipoEvento.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

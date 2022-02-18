package it.jhiptest.service.impl;

import it.jhiptest.domain.TipoEvento;
import it.jhiptest.repository.TipoEventoRepository;
import it.jhiptest.service.TipoEventoService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TipoEvento}.
 */
@Service
@Transactional
public class TipoEventoServiceImpl implements TipoEventoService {

    private final Logger log = LoggerFactory.getLogger(TipoEventoServiceImpl.class);

    private final TipoEventoRepository tipoEventoRepository;

    public TipoEventoServiceImpl(TipoEventoRepository tipoEventoRepository) {
        this.tipoEventoRepository = tipoEventoRepository;
    }

    @Override
    public TipoEvento save(TipoEvento tipoEvento) {
        log.debug("Request to save TipoEvento : {}", tipoEvento);
        return tipoEventoRepository.save(tipoEvento);
    }

    @Override
    public Optional<TipoEvento> partialUpdate(TipoEvento tipoEvento) {
        log.debug("Request to partially update TipoEvento : {}", tipoEvento);

        return tipoEventoRepository
            .findById(tipoEvento.getId())
            .map(existingTipoEvento -> {
                if (tipoEvento.getIdTipoEvento() != null) {
                    existingTipoEvento.setIdTipoEvento(tipoEvento.getIdTipoEvento());
                }
                if (tipoEvento.getDsTipoEvento() != null) {
                    existingTipoEvento.setDsTipoEvento(tipoEvento.getDsTipoEvento());
                }

                return existingTipoEvento;
            })
            .map(tipoEventoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEvento> findAll() {
        log.debug("Request to get all TipoEventos");
        return tipoEventoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoEvento> findOne(Long id) {
        log.debug("Request to get TipoEvento : {}", id);
        return tipoEventoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TipoEvento : {}", id);
        tipoEventoRepository.deleteById(id);
    }
}

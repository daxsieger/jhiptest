package it.jhiptest.service.impl;

import it.jhiptest.domain.Stato;
import it.jhiptest.repository.StatoRepository;
import it.jhiptest.service.StatoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Stato}.
 */
@Service
@Transactional
public class StatoServiceImpl implements StatoService {

    private final Logger log = LoggerFactory.getLogger(StatoServiceImpl.class);

    private final StatoRepository statoRepository;

    public StatoServiceImpl(StatoRepository statoRepository) {
        this.statoRepository = statoRepository;
    }

    @Override
    public Stato save(Stato stato) {
        log.debug("Request to save Stato : {}", stato);
        return statoRepository.save(stato);
    }

    @Override
    public Optional<Stato> partialUpdate(Stato stato) {
        log.debug("Request to partially update Stato : {}", stato);

        return statoRepository
            .findById(stato.getId())
            .map(existingStato -> {
                if (stato.getIdStadio() != null) {
                    existingStato.setIdStadio(stato.getIdStadio());
                }
                if (stato.getDsStadio() != null) {
                    existingStato.setDsStadio(stato.getDsStadio());
                }
                if (stato.getTsCambioStato() != null) {
                    existingStato.setTsCambioStato(stato.getTsCambioStato());
                }

                return existingStato;
            })
            .map(statoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Stato> findAll(Pageable pageable) {
        log.debug("Request to get all Statoes");
        return statoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stato> findOne(Long id) {
        log.debug("Request to get Stato : {}", id);
        return statoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Stato : {}", id);
        statoRepository.deleteById(id);
    }
}

package it.jhiptest.service.impl;

import it.jhiptest.domain.Stadio;
import it.jhiptest.repository.StadioRepository;
import it.jhiptest.service.StadioService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Stadio}.
 */
@Service
@Transactional
public class StadioServiceImpl implements StadioService {

    private final Logger log = LoggerFactory.getLogger(StadioServiceImpl.class);

    private final StadioRepository stadioRepository;

    public StadioServiceImpl(StadioRepository stadioRepository) {
        this.stadioRepository = stadioRepository;
    }

    @Override
    public Stadio save(Stadio stadio) {
        log.debug("Request to save Stadio : {}", stadio);
        return stadioRepository.save(stadio);
    }

    @Override
    public Optional<Stadio> partialUpdate(Stadio stadio) {
        log.debug("Request to partially update Stadio : {}", stadio);

        return stadioRepository
            .findById(stadio.getId())
            .map(existingStadio -> {
                if (stadio.getIdStadio() != null) {
                    existingStadio.setIdStadio(stadio.getIdStadio());
                }
                if (stadio.getDsStadio() != null) {
                    existingStadio.setDsStadio(stadio.getDsStadio());
                }

                return existingStadio;
            })
            .map(stadioRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Stadio> findAll(Pageable pageable) {
        log.debug("Request to get all Stadios");
        return stadioRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Stadio> findOne(Long id) {
        log.debug("Request to get Stadio : {}", id);
        return stadioRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Stadio : {}", id);
        stadioRepository.deleteById(id);
    }
}

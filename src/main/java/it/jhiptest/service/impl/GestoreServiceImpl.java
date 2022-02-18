package it.jhiptest.service.impl;

import it.jhiptest.domain.Gestore;
import it.jhiptest.repository.GestoreRepository;
import it.jhiptest.service.GestoreService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Gestore}.
 */
@Service
@Transactional
public class GestoreServiceImpl implements GestoreService {

    private final Logger log = LoggerFactory.getLogger(GestoreServiceImpl.class);

    private final GestoreRepository gestoreRepository;

    public GestoreServiceImpl(GestoreRepository gestoreRepository) {
        this.gestoreRepository = gestoreRepository;
    }

    @Override
    public Gestore save(Gestore gestore) {
        log.debug("Request to save Gestore : {}", gestore);
        return gestoreRepository.save(gestore);
    }

    @Override
    public Optional<Gestore> partialUpdate(Gestore gestore) {
        log.debug("Request to partially update Gestore : {}", gestore);

        return gestoreRepository
            .findById(gestore.getId())
            .map(existingGestore -> {
                if (gestore.getIdGestore() != null) {
                    existingGestore.setIdGestore(gestore.getIdGestore());
                }

                return existingGestore;
            })
            .map(gestoreRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Gestore> findAll(Pageable pageable) {
        log.debug("Request to get all Gestores");
        return gestoreRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Gestore> findOne(Long id) {
        log.debug("Request to get Gestore : {}", id);
        return gestoreRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Gestore : {}", id);
        gestoreRepository.deleteById(id);
    }
}

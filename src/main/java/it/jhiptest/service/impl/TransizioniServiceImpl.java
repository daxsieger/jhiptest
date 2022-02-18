package it.jhiptest.service.impl;

import it.jhiptest.domain.Transizioni;
import it.jhiptest.repository.TransizioniRepository;
import it.jhiptest.service.TransizioniService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Transizioni}.
 */
@Service
@Transactional
public class TransizioniServiceImpl implements TransizioniService {

    private final Logger log = LoggerFactory.getLogger(TransizioniServiceImpl.class);

    private final TransizioniRepository transizioniRepository;

    public TransizioniServiceImpl(TransizioniRepository transizioniRepository) {
        this.transizioniRepository = transizioniRepository;
    }

    @Override
    public Transizioni save(Transizioni transizioni) {
        log.debug("Request to save Transizioni : {}", transizioni);
        return transizioniRepository.save(transizioni);
    }

    @Override
    public Optional<Transizioni> partialUpdate(Transizioni transizioni) {
        log.debug("Request to partially update Transizioni : {}", transizioni);

        return transizioniRepository
            .findById(transizioni.getId())
            .map(existingTransizioni -> {
                if (transizioni.getIdTransizione() != null) {
                    existingTransizioni.setIdTransizione(transizioni.getIdTransizione());
                }
                if (transizioni.getDsTransizione() != null) {
                    existingTransizioni.setDsTransizione(transizioni.getDsTransizione());
                }

                return existingTransizioni;
            })
            .map(transizioniRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Transizioni> findAll(Pageable pageable) {
        log.debug("Request to get all Transizionis");
        return transizioniRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Transizioni> findOne(Long id) {
        log.debug("Request to get Transizioni : {}", id);
        return transizioniRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transizioni : {}", id);
        transizioniRepository.deleteById(id);
    }
}

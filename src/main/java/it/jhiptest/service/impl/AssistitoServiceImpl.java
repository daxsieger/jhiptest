package it.jhiptest.service.impl;

import it.jhiptest.domain.Assistito;
import it.jhiptest.repository.AssistitoRepository;
import it.jhiptest.service.AssistitoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Assistito}.
 */
@Service
@Transactional
public class AssistitoServiceImpl implements AssistitoService {

    private final Logger log = LoggerFactory.getLogger(AssistitoServiceImpl.class);

    private final AssistitoRepository assistitoRepository;

    public AssistitoServiceImpl(AssistitoRepository assistitoRepository) {
        this.assistitoRepository = assistitoRepository;
    }

    @Override
    public Assistito save(Assistito assistito) {
        log.debug("Request to save Assistito : {}", assistito);
        return assistitoRepository.save(assistito);
    }

    @Override
    public Optional<Assistito> partialUpdate(Assistito assistito) {
        log.debug("Request to partially update Assistito : {}", assistito);

        return assistitoRepository
            .findById(assistito.getId())
            .map(existingAssistito -> {
                if (assistito.getIdAssistito() != null) {
                    existingAssistito.setIdAssistito(assistito.getIdAssistito());
                }

                return existingAssistito;
            })
            .map(assistitoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assistito> findAll(Pageable pageable) {
        log.debug("Request to get all Assistitos");
        return assistitoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assistito> findOne(Long id) {
        log.debug("Request to get Assistito : {}", id);
        return assistitoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Assistito : {}", id);
        assistitoRepository.deleteById(id);
    }
}

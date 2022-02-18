package it.jhiptest.service.impl;

import it.jhiptest.domain.Processo;
import it.jhiptest.repository.ProcessoRepository;
import it.jhiptest.service.ProcessoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Processo}.
 */
@Service
@Transactional
public class ProcessoServiceImpl implements ProcessoService {

    private final Logger log = LoggerFactory.getLogger(ProcessoServiceImpl.class);

    private final ProcessoRepository processoRepository;

    public ProcessoServiceImpl(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    @Override
    public Processo save(Processo processo) {
        log.debug("Request to save Processo : {}", processo);
        return processoRepository.save(processo);
    }

    @Override
    public Optional<Processo> partialUpdate(Processo processo) {
        log.debug("Request to partially update Processo : {}", processo);

        return processoRepository
            .findById(processo.getId())
            .map(existingProcesso -> {
                if (processo.getIdProcesso() != null) {
                    existingProcesso.setIdProcesso(processo.getIdProcesso());
                }
                if (processo.getDsProcesso() != null) {
                    existingProcesso.setDsProcesso(processo.getDsProcesso());
                }

                return existingProcesso;
            })
            .map(processoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Processo> findAll(Pageable pageable) {
        log.debug("Request to get all Processos");
        return processoRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Processo> findOne(Long id) {
        log.debug("Request to get Processo : {}", id);
        return processoRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Processo : {}", id);
        processoRepository.deleteById(id);
    }
}

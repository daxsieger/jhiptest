package it.jhiptest.service.impl;

import it.jhiptest.domain.Produttore;
import it.jhiptest.repository.ProduttoreRepository;
import it.jhiptest.service.ProduttoreService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Produttore}.
 */
@Service
@Transactional
public class ProduttoreServiceImpl implements ProduttoreService {

    private final Logger log = LoggerFactory.getLogger(ProduttoreServiceImpl.class);

    private final ProduttoreRepository produttoreRepository;

    public ProduttoreServiceImpl(ProduttoreRepository produttoreRepository) {
        this.produttoreRepository = produttoreRepository;
    }

    @Override
    public Produttore save(Produttore produttore) {
        log.debug("Request to save Produttore : {}", produttore);
        return produttoreRepository.save(produttore);
    }

    @Override
    public Optional<Produttore> partialUpdate(Produttore produttore) {
        log.debug("Request to partially update Produttore : {}", produttore);

        return produttoreRepository
            .findById(produttore.getId())
            .map(existingProduttore -> {
                if (produttore.getIdProduttore() != null) {
                    existingProduttore.setIdProduttore(produttore.getIdProduttore());
                }
                if (produttore.getDsProduttore() != null) {
                    existingProduttore.setDsProduttore(produttore.getDsProduttore());
                }

                return existingProduttore;
            })
            .map(produttoreRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Produttore> findAll(Pageable pageable) {
        log.debug("Request to get all Produttores");
        return produttoreRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produttore> findOne(Long id) {
        log.debug("Request to get Produttore : {}", id);
        return produttoreRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Produttore : {}", id);
        produttoreRepository.deleteById(id);
    }
}

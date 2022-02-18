package it.jhiptest.service.impl;

import it.jhiptest.domain.Evento;
import it.jhiptest.repository.EventoRepository;
import it.jhiptest.service.EventoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Evento}.
 */
@Service
@Transactional
public class EventoServiceImpl implements EventoService {

    private final Logger log = LoggerFactory.getLogger(EventoServiceImpl.class);

    private final EventoRepository eventoRepository;

    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Evento save(Evento evento) {
        log.debug("Request to save Evento : {}", evento);
        return eventoRepository.save(evento);
    }

    @Override
    public Optional<Evento> partialUpdate(Evento evento) {
        log.debug("Request to partially update Evento : {}", evento);

        return eventoRepository
            .findById(evento.getId())
            .map(existingEvento -> {
                if (evento.getIdEvento() != null) {
                    existingEvento.setIdEvento(evento.getIdEvento());
                }
                if (evento.getTsEvento() != null) {
                    existingEvento.setTsEvento(evento.getTsEvento());
                }
                if (evento.getNote() != null) {
                    existingEvento.setNote(evento.getNote());
                }

                return existingEvento;
            })
            .map(eventoRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Evento> findAll(Pageable pageable) {
        log.debug("Request to get all Eventos");
        return eventoRepository.findAll(pageable);
    }

    public Page<Evento> findAllWithEagerRelationships(Pageable pageable) {
        return eventoRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evento> findOne(Long id) {
        log.debug("Request to get Evento : {}", id);
        return eventoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Evento : {}", id);
        eventoRepository.deleteById(id);
    }
}

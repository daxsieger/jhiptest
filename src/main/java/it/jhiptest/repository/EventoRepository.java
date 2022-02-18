package it.jhiptest.repository;

import it.jhiptest.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Evento entity.
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Query(
        value = "select distinct evento from Evento evento left join fetch evento.statis",
        countQuery = "select count(distinct evento) from Evento evento"
    )
    Page<Evento> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct evento from Evento evento left join fetch evento.statis")
    List<Evento> findAllWithEagerRelationships();

    @Query("select evento from Evento evento left join fetch evento.statis where evento.id =:id")
    Optional<Evento> findOneWithEagerRelationships(@Param("id") Long id);
}

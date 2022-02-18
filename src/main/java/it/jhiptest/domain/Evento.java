package it.jhiptest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Evento.
 */
@Entity
@Table(name = "evento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Evento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "ts_evento")
    private Instant tsEvento;

    @Column(name = "note")
    private String note;

    @OneToOne
    @JoinColumn(unique = true)
    private Assistito assistito;

    @OneToOne
    @JoinColumn(unique = true)
    private TipoEvento tipo;

    @OneToOne
    @JoinColumn(unique = true)
    private Gestore gestore;

    @ManyToOne
    private Produttore origine;

    @ManyToMany
    @JoinTable(
        name = "rel_evento__stati",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "stati_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "stadio", "eventis" }, allowSetters = true)
    private Set<Stato> statis = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Evento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdEvento() {
        return this.idEvento;
    }

    public Evento idEvento(Long idEvento) {
        this.setIdEvento(idEvento);
        return this;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public Instant getTsEvento() {
        return this.tsEvento;
    }

    public Evento tsEvento(Instant tsEvento) {
        this.setTsEvento(tsEvento);
        return this;
    }

    public void setTsEvento(Instant tsEvento) {
        this.tsEvento = tsEvento;
    }

    public String getNote() {
        return this.note;
    }

    public Evento note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Assistito getAssistito() {
        return this.assistito;
    }

    public void setAssistito(Assistito assistito) {
        this.assistito = assistito;
    }

    public Evento assistito(Assistito assistito) {
        this.setAssistito(assistito);
        return this;
    }

    public TipoEvento getTipo() {
        return this.tipo;
    }

    public void setTipo(TipoEvento tipoEvento) {
        this.tipo = tipoEvento;
    }

    public Evento tipo(TipoEvento tipoEvento) {
        this.setTipo(tipoEvento);
        return this;
    }

    public Gestore getGestore() {
        return this.gestore;
    }

    public void setGestore(Gestore gestore) {
        this.gestore = gestore;
    }

    public Evento gestore(Gestore gestore) {
        this.setGestore(gestore);
        return this;
    }

    public Produttore getOrigine() {
        return this.origine;
    }

    public void setOrigine(Produttore produttore) {
        this.origine = produttore;
    }

    public Evento origine(Produttore produttore) {
        this.setOrigine(produttore);
        return this;
    }

    public Set<Stato> getStatis() {
        return this.statis;
    }

    public void setStatis(Set<Stato> statoes) {
        this.statis = statoes;
    }

    public Evento statis(Set<Stato> statoes) {
        this.setStatis(statoes);
        return this;
    }

    public Evento addStati(Stato stato) {
        this.statis.add(stato);
        stato.getEventis().add(this);
        return this;
    }

    public Evento removeStati(Stato stato) {
        this.statis.remove(stato);
        stato.getEventis().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Evento)) {
            return false;
        }
        return id != null && id.equals(((Evento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Evento{" +
            "id=" + getId() +
            ", idEvento=" + getIdEvento() +
            ", tsEvento='" + getTsEvento() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

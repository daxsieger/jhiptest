package it.jhiptest.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TipoEvento.
 */
@Entity
@Table(name = "tipo_evento")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TipoEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_tipo_evento")
    private Long idTipoEvento;

    @Column(name = "ds_tipo_evento")
    private String dsTipoEvento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TipoEvento id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTipoEvento() {
        return this.idTipoEvento;
    }

    public TipoEvento idTipoEvento(Long idTipoEvento) {
        this.setIdTipoEvento(idTipoEvento);
        return this;
    }

    public void setIdTipoEvento(Long idTipoEvento) {
        this.idTipoEvento = idTipoEvento;
    }

    public String getDsTipoEvento() {
        return this.dsTipoEvento;
    }

    public TipoEvento dsTipoEvento(String dsTipoEvento) {
        this.setDsTipoEvento(dsTipoEvento);
        return this;
    }

    public void setDsTipoEvento(String dsTipoEvento) {
        this.dsTipoEvento = dsTipoEvento;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TipoEvento)) {
            return false;
        }
        return id != null && id.equals(((TipoEvento) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TipoEvento{" +
            "id=" + getId() +
            ", idTipoEvento=" + getIdTipoEvento() +
            ", dsTipoEvento='" + getDsTipoEvento() + "'" +
            "}";
    }
}

package it.jhiptest.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Stadio.
 */
@Entity
@Table(name = "stadio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Stadio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_stadio")
    private Long idStadio;

    @Column(name = "ds_stadio")
    private String dsStadio;

    @ManyToOne
    private Processo processo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Stadio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdStadio() {
        return this.idStadio;
    }

    public Stadio idStadio(Long idStadio) {
        this.setIdStadio(idStadio);
        return this;
    }

    public void setIdStadio(Long idStadio) {
        this.idStadio = idStadio;
    }

    public String getDsStadio() {
        return this.dsStadio;
    }

    public Stadio dsStadio(String dsStadio) {
        this.setDsStadio(dsStadio);
        return this;
    }

    public void setDsStadio(String dsStadio) {
        this.dsStadio = dsStadio;
    }

    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Stadio processo(Processo processo) {
        this.setProcesso(processo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Stadio)) {
            return false;
        }
        return id != null && id.equals(((Stadio) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Stadio{" +
            "id=" + getId() +
            ", idStadio=" + getIdStadio() +
            ", dsStadio='" + getDsStadio() + "'" +
            "}";
    }
}

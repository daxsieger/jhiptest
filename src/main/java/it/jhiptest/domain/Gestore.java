package it.jhiptest.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Gestore.
 */
@Entity
@Table(name = "gestore")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Gestore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_gestore")
    private Long idGestore;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Gestore id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdGestore() {
        return this.idGestore;
    }

    public Gestore idGestore(Long idGestore) {
        this.setIdGestore(idGestore);
        return this;
    }

    public void setIdGestore(Long idGestore) {
        this.idGestore = idGestore;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gestore)) {
            return false;
        }
        return id != null && id.equals(((Gestore) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Gestore{" +
            "id=" + getId() +
            ", idGestore=" + getIdGestore() +
            "}";
    }
}

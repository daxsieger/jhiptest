package it.jhiptest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transizioni.
 */
@Entity
@Table(name = "transizioni")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transizioni implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "id_transizione")
    private Long idTransizione;

    @Column(name = "ds_transizione")
    private String dsTransizione;

    @ManyToOne
    private Processo processo;

    @ManyToOne
    @JsonIgnoreProperties(value = { "processo" }, allowSetters = true)
    private Stadio stadioIniziale;

    @ManyToOne
    @JsonIgnoreProperties(value = { "processo" }, allowSetters = true)
    private Stadio stadioFinale;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transizioni id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdTransizione() {
        return this.idTransizione;
    }

    public Transizioni idTransizione(Long idTransizione) {
        this.setIdTransizione(idTransizione);
        return this;
    }

    public void setIdTransizione(Long idTransizione) {
        this.idTransizione = idTransizione;
    }

    public String getDsTransizione() {
        return this.dsTransizione;
    }

    public Transizioni dsTransizione(String dsTransizione) {
        this.setDsTransizione(dsTransizione);
        return this;
    }

    public void setDsTransizione(String dsTransizione) {
        this.dsTransizione = dsTransizione;
    }

    public Processo getProcesso() {
        return this.processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public Transizioni processo(Processo processo) {
        this.setProcesso(processo);
        return this;
    }

    public Stadio getStadioIniziale() {
        return this.stadioIniziale;
    }

    public void setStadioIniziale(Stadio stadio) {
        this.stadioIniziale = stadio;
    }

    public Transizioni stadioIniziale(Stadio stadio) {
        this.setStadioIniziale(stadio);
        return this;
    }

    public Stadio getStadioFinale() {
        return this.stadioFinale;
    }

    public void setStadioFinale(Stadio stadio) {
        this.stadioFinale = stadio;
    }

    public Transizioni stadioFinale(Stadio stadio) {
        this.setStadioFinale(stadio);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transizioni)) {
            return false;
        }
        return id != null && id.equals(((Transizioni) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transizioni{" +
            "id=" + getId() +
            ", idTransizione=" + getIdTransizione() +
            ", dsTransizione='" + getDsTransizione() + "'" +
            "}";
    }
}

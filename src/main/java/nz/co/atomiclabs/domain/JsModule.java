package nz.co.atomiclabs.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A JsModule.
 */
@Audited
@Entity
@Table(name = "js_module")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class JsModule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "name", length = 20, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "source", nullable = false, columnDefinition = "text")
    private String source;

    @NotNull
    @Column(name = "auto_load", nullable = false)
    private Boolean autoLoad;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public JsModule name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public JsModule source(String source) {
        this.source = source;
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean isAutoLoad() {
        return autoLoad;
    }

    public JsModule autoLoad(Boolean autoLoad) {
        this.autoLoad = autoLoad;
        return this;
    }

    public void setAutoLoad(Boolean autoLoad) {
        this.autoLoad = autoLoad;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JsModule)) {
            return false;
        }
        return id != null && id.equals(((JsModule) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "JsModule{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", source='" + getSource() + "'" +
            ", autoLoad='" + isAutoLoad() + "'" +
            "}";
    }
}

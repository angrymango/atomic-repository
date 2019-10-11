package nz.co.atomiclabs.domain;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Node.
 */
@Audited
@Entity
@Table(name = "node")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Node extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "content", nullable = false, columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode content;

    @Generated(GenerationTime.ALWAYS)
    @Column(name= "_search", columnDefinition = "tsvector")
    private String search;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "parent_path", columnDefinition = "ltree")
    private String parentPath;

    @ManyToOne
    private Node parent;

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

    public Node name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JsonNode getContent() {
        return content;
    }

    public Node content(JsonNode content) {
        this.content = content;
        return this;
    }

    public void setContent(JsonNode content) {
        this.content = content;
    }

    public String getSearch() {
        return search;
    }

    public Node search(String search) {
        this.search = search;
        return this;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getParentPath() {
        return parentPath;
    }

    public Node parentPath(String parentPath) {
        this.parentPath = parentPath;
        return this;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Node getParent() {
        return parent;
    }

    public Node parent(Node node) {
        this.parent = node;
        return this;
    }

    public void setParent(Node node) {
        this.parent = node;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        return id != null && id.equals(((Node) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Node{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", content='" + getContent() + "'" +
            ", search='" + getSearch() + "'" +
            ", parentPath='" + getParentPath() + "'" +
            "}";
    }
}

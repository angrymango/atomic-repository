package nz.co.atomiclabs.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import nz.co.atomiclabs.service.filter.JsonFilter;
import nz.co.atomiclabs.service.filter.LTreeFilter;
import nz.co.atomiclabs.service.filter.SearchFilter;

/**
 * Criteria class for the {@link nz.co.atomiclabs.domain.Node} entity. This class is used
 * in {@link nz.co.atomiclabs.web.rest.NodeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /nodes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class NodeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private JsonFilter content;

    private SearchFilter search;

    private LongFilter parentId;

    private LTreeFilter parentPath;

    public NodeCriteria(){
    }

    public NodeCriteria(NodeCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.content = other.content == null ? null : other.content.copy();
        this.search = other.search == null ? null : other.search.copy();
        this.parentPath = other.parentPath == null ? null : other.parentPath.copy();
        this.parentId = other.parentId == null ? null : other.parentId.copy();
    }

    @Override
    public NodeCriteria copy() {
        return new NodeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public JsonFilter getContent() {
        return content;
    }

    public void setContent(JsonFilter content) {
        this.content = content;
    }

    public SearchFilter getSearch() {
        return search;
    }

    public NodeCriteria setSearch(SearchFilter search) {
        this.search = search;
        return this;
    }

    public LTreeFilter getParentPath() {
        return parentPath;
    }

    public void setParentPath(LTreeFilter parentPath) {
        this.parentPath = parentPath;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NodeCriteria that = (NodeCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(content, that.content) &&
            Objects.equals(content, that.content) &&
            Objects.equals(search, that.search) &&
            Objects.equals(parentPath, that.parentPath) &&
            Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        content,
        search,
        parentPath,
        parentId
        );
    }

    @Override
    public String toString() {
        return "NodeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (content != null ? "content=" + content + ", " : "") +
                (search != null ? "search=" + search + ", " : "") +
                (parentPath != null ? "parentPath=" + parentPath + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
            "}";
    }

}

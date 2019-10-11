package nz.co.atomiclabs.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link nz.co.atomiclabs.domain.JsModule} entity. This class is used
 * in {@link nz.co.atomiclabs.web.rest.JsModuleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /js-modules?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class JsModuleCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter source;

    private BooleanFilter autoLoad;

    public JsModuleCriteria(){
    }

    public JsModuleCriteria(JsModuleCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.source = other.source == null ? null : other.source.copy();
        this.autoLoad = other.autoLoad == null ? null : other.autoLoad.copy();
    }

    @Override
    public JsModuleCriteria copy() {
        return new JsModuleCriteria(this);
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

    public StringFilter getSource() {
        return source;
    }

    public void setSource(StringFilter source) {
        this.source = source;
    }

    public BooleanFilter getAutoLoad() {
        return autoLoad;
    }

    public void setAutoLoad(BooleanFilter autoLoad) {
        this.autoLoad = autoLoad;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JsModuleCriteria that = (JsModuleCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(source, that.source) &&
            Objects.equals(autoLoad, that.autoLoad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        source,
        autoLoad
        );
    }

    @Override
    public String toString() {
        return "JsModuleCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (source != null ? "source=" + source + ", " : "") +
                (autoLoad != null ? "autoLoad=" + autoLoad + ", " : "") +
            "}";
    }

}

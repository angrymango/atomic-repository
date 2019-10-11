package nz.co.atomiclabs.service.filter;

import java.io.Serializable;
import java.util.Objects;

public class LTreeFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ancestor;
    private String descendant;
    private String query;
    private String matches;
    private String equals;

    public LTreeFilter() {
    }

    public LTreeFilter(final LTreeFilter lTreeFilter) {
        this.ancestor = lTreeFilter.getAncestor();
        this.descendant = lTreeFilter.getDescendant();
        this.query = lTreeFilter.getQuery();
        this.matches = lTreeFilter.getMatches();
        this.equals = lTreeFilter.getEquals();
    }

    public String getAncestor() {
        return ancestor;
    }

    public LTreeFilter setAncestor(String ancestor) {
        this.ancestor = ancestor;
        return this;
    }

    public String getDescendant() {
        return descendant;
    }

    public LTreeFilter setDescendant(String descendant) {
        this.descendant = descendant;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public LTreeFilter setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getMatches() {
        return matches;
    }

    public LTreeFilter setMatches(String matches) {
        this.matches = matches;
        return this;
    }

    public String getEquals() {
        return equals;
    }

    public LTreeFilter setEquals(String equals) {
        this.equals = equals;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LTreeFilter that = (LTreeFilter) o;
        return Objects.equals(ancestor, that.ancestor) &&
            Objects.equals(descendant, that.descendant) &&
            Objects.equals(query, that.query) &&
            Objects.equals(matches, that.matches) &&
            Objects.equals(equals, that.equals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ancestor, descendant, query, matches, equals);
    }

    @Override
    public String toString() {
        return "LTreeFilter{" +
            "ancestor='" + ancestor + '\'' +
            ", descendant='" + descendant + '\'' +
            ", query='" + query + '\'' +
            ", matches='" + matches + '\'' +
            ", equals='" + equals + '\'' +
            '}';
    }

    public LTreeFilter copy() {
        return new LTreeFilter(this);
    }
}

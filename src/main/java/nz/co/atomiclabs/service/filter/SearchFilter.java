package nz.co.atomiclabs.service.filter;

import java.io.Serializable;
import java.util.Objects;

public class SearchFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String matches;

    public SearchFilter() {
    }

    public SearchFilter(final SearchFilter filter) {
        this.matches = filter.getMatches();
    }

    public String getMatches() {
        return matches;
    }

    public SearchFilter setMatches(String matches) {
        this.matches = matches;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFilter that = (SearchFilter) o;
        return Objects.equals(matches, that.matches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matches);
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
            "matches='" + matches + '\'' +
            '}';
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link nz.co.atomiclabs.service.filter.SearchFilter} object.
     */
    public SearchFilter copy() {
        return new SearchFilter(this);
    }
}

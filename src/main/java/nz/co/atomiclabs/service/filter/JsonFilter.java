package nz.co.atomiclabs.service.filter;

import java.io.Serializable;
import java.util.Objects;

public class JsonFilter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String contains;
    private String exists;
    private String any;
    private String all;
    private String pathExists;
    private String pathMatches;

    public JsonFilter() {
    }

    /**
     * <p>Constructor for JsonFilter.</p>
     *
     * @param filter a {@link nz.co.atomiclabs.service.filter.JsonFilter} object.
     */
    public JsonFilter(final JsonFilter filter) {
        this.contains = filter.contains;
        this.exists = filter.exists;
        this.any = filter.any;
        this.all = filter.all;
        this.pathExists = filter.pathExists;
        this.pathMatches = filter.pathMatches;
    }

    public String getContains() {
        return contains;
    }

    public JsonFilter setContains(String contains) {
        this.contains = contains;
        return this;
    }

    public String getExists() {
        return exists;
    }

    public JsonFilter setExists(String exists) {
        this.exists = exists;
        return this;
    }

    public String getAny() {
        return any;
    }

    public JsonFilter setAny(String any) {
        this.any = any;
        return this;
    }

    public String getAll() {
        return all;
    }

    public JsonFilter setAll(String all) {
        this.all = all;
        return this;
    }

    public String getPathExists() {
        return pathExists;
    }

    public void setPathExists(String pathExists) {
        this.pathExists = pathExists;
    }

    public String getPathMatches() {
        return pathMatches;
    }

    public void setPathMatches(String pathMatches) {
        this.pathMatches = pathMatches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonFilter that = (JsonFilter) o;
        return Objects.equals(contains, that.contains) &&
            Objects.equals(exists, that.exists) &&
            Objects.equals(any, that.any) &&
            Objects.equals(all, that.all) &&
            Objects.equals(pathExists, that.pathExists) &&
            Objects.equals(pathMatches, that.pathMatches);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contains, exists, any, all, pathExists, pathMatches);
    }

    @Override
    public String toString() {
        return "JsonFilter{" +
            "contains='" + contains + '\'' +
            ", exists='" + exists + '\'' +
            ", any='" + any + '\'' +
            ", all='" + all + '\'' +
            ", pathExists='" + pathExists + '\'' +
            ", pathMatches='" + pathMatches + '\'' +
            '}';
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link nz.co.atomiclabs.service.filter.JsonFilter} object.
     */
    public JsonFilter copy() {
        return new JsonFilter(this);
    }
}

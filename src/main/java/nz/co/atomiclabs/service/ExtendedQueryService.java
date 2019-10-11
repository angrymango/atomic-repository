package nz.co.atomiclabs.service;

import io.github.jhipster.service.QueryService;
import nz.co.atomiclabs.repository.extension.ExtendedPostgreSQLDialect;
import nz.co.atomiclabs.service.filter.JsonFilter;
import nz.co.atomiclabs.service.filter.LTreeFilter;
import nz.co.atomiclabs.service.filter.SearchFilter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.function.Function;

abstract class ExtendedQueryService<ENTITY> extends QueryService<ENTITY> {

    // JsonB

    <X> Specification<ENTITY> buildSpecification(JsonFilter filter, SingularAttribute<? super ENTITY, X> field) {
        return this.buildSpecification(filter, (root) -> root.get(field));
    }

    private <X> Specification<ENTITY> buildSpecification(JsonFilter filter, Function<Root<ENTITY>, Expression<X>> metaclassFunction) {
        if (filter.getAll() != null) {
            return this.jsonExistsAll(metaclassFunction, filter.getAll());
        } else if (filter.getContains() != null) {
            return this.jsonContains(metaclassFunction, filter.getContains());
        } else if (filter.getAny() != null) {
            return this.jsonExistsAny(metaclassFunction, filter.getAny());
        } else if (filter.getExists() != null) {
            return this.jsonExists(metaclassFunction, filter.getExists());
        } else if (filter.getPathExists() != null) {
            return this.jsonPathExists(metaclassFunction, filter.getPathExists());
        } else if (filter.getPathMatches() != null) {
            return this.jsonPathMatches(metaclassFunction, filter.getPathMatches());
        }

        return null;
    }

    private <X> Specification<ENTITY> jsonExistsAll(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_EXISTS_ALL, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> jsonContains(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_CONTAINS, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> jsonExistsAny(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_EXISTS_ANY, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> jsonExists(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_EXISTS, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> jsonPathExists(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_PATH_EXISTS, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> jsonPathMatches(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.JSONB_PATH_MATCHES, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    <X> Specification<ENTITY> buildSpecification(SearchFilter filter, SingularAttribute<? super ENTITY, X> field) {
        return this.buildSpecification(filter, (root) -> root.get(field));
    }

    private <X> Specification<ENTITY> buildSpecification(SearchFilter filter, Function<Root<ENTITY>, Expression<X>> metaclassFunction) {
        if (filter.getMatches() != null) {
            return this.searchMatches(metaclassFunction, filter.getMatches());
        }

        return null;
    }

    private <X> Specification<ENTITY> searchMatches(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> {
            Expression<?> rank = builder.function(ExtendedPostgreSQLDialect.FTS_RANK, Object.class, metaclassFunction.apply(root), builder.literal(value));
            query.orderBy(builder.desc(rank));
            return builder.isTrue(builder.function(ExtendedPostgreSQLDialect.FTS, Boolean.class, metaclassFunction.apply(root), builder.literal(value)));
        };
    }

    // LTree

    <X> Specification<ENTITY> buildSpecification(LTreeFilter filter, SingularAttribute<? super ENTITY, X> field) {
        return this.buildSpecification(filter, (root) -> root.get(field));
    }

    private <X> Specification<ENTITY> buildSpecification(LTreeFilter filter, Function<Root<ENTITY>, Expression<X>> metaclassFunction) {
        if (filter.getAncestor() != null) {
            return this.lTreeAncestor(metaclassFunction, filter.getAncestor());
        } else if (filter.getDescendant() != null) {
            return this.lTreeDescendent(metaclassFunction, filter.getDescendant());
        } else if (filter.getQuery() != null) {
            return this.lTreeQuery(metaclassFunction, filter.getQuery());
        } else if (filter.getMatches() != null) {
            return this.lTreeMatches(metaclassFunction, filter.getMatches());
        } else if (filter.getEquals() != null) {
            return lTreeEquals(metaclassFunction, filter.getEquals());
        }

        return null;
    }

    private <X> Specification<ENTITY> lTreeAncestor(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.LTREE_ISPARENT, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> lTreeDescendent(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.LTREE_RISPARENT, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> lTreeQuery(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.LTQ_REGEX, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> lTreeMatches(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.LTXTQ_EXEC, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }

    private <X> Specification<ENTITY> lTreeEquals(Function<Root<ENTITY>, Expression<X>> metaclassFunction, String value) {
        return (root, query, builder) -> builder.equal(builder.function(ExtendedPostgreSQLDialect.LTREE_EQUALS, Boolean.class, metaclassFunction.apply(root), builder.literal(value)), true);
    }
}

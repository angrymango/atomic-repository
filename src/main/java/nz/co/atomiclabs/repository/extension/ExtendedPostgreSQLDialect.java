package nz.co.atomiclabs.repository.extension;

import nz.co.atomiclabs.repository.extension.function.*;
import nz.co.atomiclabs.repository.extension.function.fts.PgFullTextFunction;
import nz.co.atomiclabs.repository.extension.function.fts.PgFullTextRankFunction;
import nz.co.atomiclabs.repository.extension.function.jsonb.*;
import nz.co.atomiclabs.repository.extension.function.ltree.*;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect;
import org.hibernate.type.DoubleType;
import org.hibernate.type.ObjectType;

public class ExtendedPostgreSQLDialect extends PostgisPG95Dialect {
    public static final String FTS = "fts";
    public static final String FTS_RANK = "fts_rank";
    public static final String JSONB_EXISTS_ALL = "jsonb_exists_all";
    public static final String JSONB_CONTAINS = "jsonb_contains";
    public static final String JSONB_EXISTS = "jsonb_exists";
    public static final String JSONB_EXISTS_ANY = "jsonb_exists_any";
    public static final String JSONB_PATH_EXISTS = "jsonb_path_exists";
    public static final String JSONB_PATH_MATCHES = "jsonb_path_matches";
    public static final String LTQ_REGEX = "ltq_regex";
    public static final String LTREE_ISPARENT = "ltree_isparent";
    public static final String LTREE_RISPARENT = "ltree_risparent";
    public static final String LTXTQ_EXEC = "ltxtq_exec";
    public static final String LTREE_EQUALS = "ltree_equals";

    private static final String TS_RANK = "ts_rank";
    private static final String TO_TSQUERY = "to_tsquery";
    private static final String TO_INTERVALS = "to_intervals";

    public ExtendedPostgreSQLDialect() {
        registerFunction(FTS, new PgFullTextFunction());
        registerFunction(FTS_RANK, new PgFullTextRankFunction());
        registerFunction(TS_RANK, new StandardSQLFunction(TS_RANK, DoubleType.INSTANCE));
        registerFunction(TO_TSQUERY, new StandardSQLFunction(TO_TSQUERY, ObjectType.INSTANCE));
        registerFunction(TO_INTERVALS, new ToIntervalsFunction());

        registerFunction(JSONB_EXISTS_ALL, new JsonBinaryExistsAllFunction());
        registerFunction(JSONB_CONTAINS, new JsonBinaryContainsFunction());
        registerFunction(JSONB_EXISTS, new JsonBinaryExistsFunction());
        registerFunction(JSONB_EXISTS_ANY, new JsonBinaryExistsFunction());
        registerFunction(JSONB_PATH_EXISTS, new JsonBinaryPathExistsFunction());
        registerFunction(JSONB_PATH_MATCHES, new JsonBinaryPathMatchFunction());

        registerFunction(LTQ_REGEX, new LTQRegexFunction());
        registerFunction(LTREE_ISPARENT, new LTreeIsParentFunction());
        registerFunction(LTREE_RISPARENT, new LTreeRIsParentFunction());
        registerFunction(LTXTQ_EXEC, new LTxtQExecFunction());
        registerFunction(LTREE_EQUALS, new LTreeEqualsFunction());
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(
                typeContributions,
                serviceRegistry
        );
    }
}

package nz.co.atomiclabs.repository.extension.function.fts;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.ObjectType;
import org.hibernate.type.Type;

import java.util.List;

public class PgFullTextRankFunction implements SQLFunction {

    @Override
    public String render(Type type, List args, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        String field = (String) args.get(0);
        String value = (String) args.get(1);
        return  "ts_rank_cd(" + field + ", to_tsquery(" + value + "))";
    }

    @Override
    public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
        return ObjectType.INSTANCE;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }
}


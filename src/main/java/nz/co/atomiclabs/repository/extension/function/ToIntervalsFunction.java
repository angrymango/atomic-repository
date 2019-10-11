package nz.co.atomiclabs.repository.extension.function;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import java.util.List;

public class ToIntervalsFunction implements SQLFunction {
    @Override
    public String render(Type type, List args, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        if(args.size() < 3) {
            throw new IllegalArgumentException("The function must be passed 3 arguments");
        }

        String field = (String) args.get(0);
        String from = (String) args.get(1);
        String to = (String) args.get(1);
        return  "to_intervals(" + field + "," + from + "," + to + ")";
    }

    @Override
    public boolean hasArguments() {
        return false;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public Type getReturnType(Type type, Mapping mapping) throws QueryException {
        return null;
    }
}

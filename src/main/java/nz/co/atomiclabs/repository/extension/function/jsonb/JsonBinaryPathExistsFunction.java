package nz.co.atomiclabs.repository.extension.function.jsonb;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import java.util.List;

public class JsonBinaryPathExistsFunction implements SQLFunction {
    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @Override
    public Type getReturnType(Type type, Mapping mapping) throws QueryException {
        return new BooleanType();
    }

    @Override
    public String render(Type type, List args, SessionFactoryImplementor sessionFactoryImplementor) throws QueryException {
        String field = (String) args.get(0);
        String path = (String) args.get(1);

        if(args.size() > 3) {
            String vars = (String) args.get(2);
            String silent = (String) args.get(3);
            return String.format("jsonb_path_exists(%s::jsonb, %s::jsonpath, %s::jsonb, %s)", field, path, vars, silent);
        } else if(args.size() > 2) {
            String vars = (String) args.get(2);
            return String.format("jsonb_path_exists(%s::jsonb, %s::jsonpath, %s::jsonb)", field, path, vars);
        }

        return String.format("jsonb_path_exists(%s::jsonb, %s::jsonpath)", field, path);
    }
}

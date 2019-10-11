package nz.co.atomiclabs.config;

import nz.co.atomiclabs.repository.extension.ContextualConnectionProvider;
import org.hibernate.cfg.Environment;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfiguration implements HibernatePropertiesCustomizer {
    private final ContextualConnectionProvider contextualConnectionProvider;

    public HibernateConfiguration(ContextualConnectionProvider contextualConnectionProvider) {
        this.contextualConnectionProvider = contextualConnectionProvider;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(Environment.CONNECTION_PROVIDER, contextualConnectionProvider);
    }
}

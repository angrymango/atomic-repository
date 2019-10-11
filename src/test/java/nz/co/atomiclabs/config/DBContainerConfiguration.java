package nz.co.atomiclabs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

@Configuration
public class DBContainerConfiguration {
    public static PostgreSQLContainer postgreSQLContainer =
            (PostgreSQLContainer)new PostgreSQLContainer(System.getProperty("APPLICATION_POSTGRES_DOCKER_IMAGE", "lovearuis/postgres-plv8:12"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres")
            .withStartupTimeout(Duration.ofSeconds(600));

    static {
        postgreSQLContainer.start();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource(){
            @Override
            public Connection getConnection() throws SQLException {
                Connection conn = super.getConnection();
                conn.setAutoCommit(false);
                return conn;
            }
        };
        ds.setUrl(postgreSQLContainer.getJdbcUrl());
        ds.setUsername(postgreSQLContainer.getUsername());
        ds.setPassword(postgreSQLContainer.getPassword());
        ds.setDriverClassName(postgreSQLContainer.getDriverClassName());
        return ds;
    }
}

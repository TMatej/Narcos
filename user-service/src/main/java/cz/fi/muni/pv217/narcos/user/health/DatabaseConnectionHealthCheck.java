package cz.fi.muni.pv217.narcos.user.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Matej Turek
 */
@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {
    private static final Logger LOG = Logger.getLogger(DatabaseConnectionHealthCheck.class);

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String databaseUrl;

    @ConfigProperty(name = "quarkus.datasource.username")
    String databaseUsername;

    @ConfigProperty(name = "quarkus.datasource.password")
    String databasePassword;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("User service readiness check");

        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            responseBuilder.up();
        } catch (SQLException e) {
            LOG.error("Failed to establish connection to database, url: " + databaseUrl);
            LOG.error(e);
            responseBuilder.down();
        }

        return responseBuilder.build();
    }
}

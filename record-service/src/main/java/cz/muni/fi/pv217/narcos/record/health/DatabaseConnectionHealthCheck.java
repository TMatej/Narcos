package cz.muni.fi.pv217.narcos.record.health;

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

    private final String hcName = "Record service readiness check";

    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(hcName);

        try (Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)) {
            responseBuilder.up();
        } catch (SQLException ex) {
            LOG.errorf("Failed to establish connection to database \"{}\"", databaseUrl);
            LOG.error(ex);
            responseBuilder.down();
        }

        return responseBuilder.build();
    }
}

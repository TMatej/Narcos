package cz.fi.muni.pv217.narcos.user.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Matej Turek
 */
@Startup
@ApplicationScoped
public class StartupHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("User service startup check");
    }
}

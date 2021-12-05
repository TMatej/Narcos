package cz.fi.muni.pv217.narcos.medicine.health;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Matej Turek
 */
@Startup
@ApplicationScoped
public class StartupHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Medicine service startup check");
    }
}

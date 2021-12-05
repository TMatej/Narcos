package cz.muni.fi.pv217.narcos.pharmacy.health;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class StartUpHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Pharmacy service startup check");
    }
}

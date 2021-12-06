package cz.fi.muni.pv217.narcos.user.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Matej Turek
 */
@Liveness
@ApplicationScoped

public class LivenessHealthCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("User service liveness check");
    }
}

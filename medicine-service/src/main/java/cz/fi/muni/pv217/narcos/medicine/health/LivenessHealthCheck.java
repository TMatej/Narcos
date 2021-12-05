package cz.fi.muni.pv217.narcos.medicine.health;

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
        return HealthCheckResponse.up("Medicine service liveness check");
    }
}

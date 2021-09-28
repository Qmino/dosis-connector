package be.vlaio.dosis.connector.springconf;

import be.vlaio.dosis.connector.common.operational.PollerSpecification;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="dosisgateway.poller")
public class PollersConfiguration {
    private List<PollerSpecification> instances;

    public List<PollerSpecification> getInstances() {
        return instances;
    }

    public void setInstances(List<PollerSpecification> instances) {
        this.instances = instances;
    }
}

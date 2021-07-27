package be.vlaio.dosis.connector.springconf;

import be.vlaio.dosis.connector.Poller;
import be.vlaio.dosis.connector.common.PollerSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.function.Function;

@Configuration
public class PollerFactoryConfig {

    @Bean
    public Function<PollerSpecification, Poller> pollerFactory() {
        return arg -> createPoller(arg);
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public Poller createPoller(PollerSpecification spec) {
        return new Poller(spec);
    }
}

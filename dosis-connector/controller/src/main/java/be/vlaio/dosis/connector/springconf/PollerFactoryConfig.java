package be.vlaio.dosis.connector.springconf;

import be.vlaio.dosis.connector.poller.DosisItemFactory;
import be.vlaio.dosis.connector.poller.Poller;
import be.vlaio.dosis.connector.common.PollerSpecification;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.function.BiFunction;
import java.util.function.Function;

@Configuration
public class PollerFactoryConfig {

    @Bean
    public Function<PollerSpecification, BiFunction<WorkInProgress, DosisItemFactory, Poller>> pollerFactory() {
        return arg -> (wip, dif) -> createPoller(arg, wip, dif);
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public Poller createPoller(PollerSpecification spec, WorkInProgress wip, DosisItemFactory factory) {
        return new Poller(spec, wip, factory);
    }
}

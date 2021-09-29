package be.vlaio.dosis.connector;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.event.AfterTestClassEvent;

public class WireMockInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(9090));
        wireMockServer.start();

        configurableApplicationContext
                .getBeanFactory()
                .registerSingleton("wireMockServer", wireMockServer);

        configurableApplicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent || applicationEvent instanceof AfterTestClassEvent) {
                wireMockServer.stop();
            }
        });
    }
}

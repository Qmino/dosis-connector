package be.vlaio.dosis.connector;

import be.vlaio.dosis.connector.common.PollerSpecification;
import be.vlaio.dosis.connector.common.PollerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Lazy
@Component
public class Poller {

    private static final Logger LOGGER = LoggerFactory.getLogger(be.vlaio.dosis.connector.Poller.class);
    private Random random = new Random();
    private String baseUrl;
    private String name;

    private int lastProcessed;
    private LocalDateTime lastFetched;
    private LocalDateTime lastRetrieved;

    public Poller(PollerSpecification spec) {
        this.baseUrl = spec.getUrl();
        this.name = spec.getName();
    }

    @Scheduled(fixedDelayString = "${dosisgateway.poller.delay}")
    public void reportCurrentTime() {
        if (random.nextBoolean()) {
            lastProcessed++;
            lastRetrieved = LocalDateTime.now();
        }
        lastFetched = LocalDateTime.now();
    }

    public PollerStatus getStatus() {
        return new PollerStatus.Builder()
                .withActive(true)
                .withCurrentItem(lastProcessed)
                .withLastPoll(lastRetrieved)
                .withNbItemsRetrieved(lastProcessed)
                .withLastElementRetrievedAt(lastFetched)
                .build();
    }
}

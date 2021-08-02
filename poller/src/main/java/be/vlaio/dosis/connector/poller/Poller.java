package be.vlaio.dosis.connector.poller;

import be.vlaio.dosis.connector.common.PollerSpecification;
import be.vlaio.dosis.connector.common.PollerStatus;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.DossierbeheersysteemFetcher;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.FetchException;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusTO;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Lazy
@Component
public class Poller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Poller.class);
    @Autowired
    private WorkInProgress wip;
    // Permanent items
    private final String baseUrl;
    private final String name;
    private final DossierbeheersysteemFetcher itemFetcher;
    // Current status
    private boolean active;
    private int lastProcessed;
    private LocalDateTime lastFetched;
    private LocalDateTime lastRetrieved;

    /**
     * Creates a new poller based on a pollerspecification
     * @param spec the spec containing the name and base url of the poller
     */
    public Poller(PollerSpecification spec) {
        this.baseUrl = spec.getUrl();
        this.name = spec.getName();
        this.itemFetcher = new DossierbeheersysteemFetcher(baseUrl);
    }

    @Scheduled(fixedDelayString = "${dosisgateway.poller.delay}")
    public void fetchItems() {
        if (active && wip.readyToAcceptNewWork()) {
            try {
                DossierStatusCollectionTO fetched = itemFetcher.fetchItems(lastProcessed, 100);
                for (DossierStatusTO item: fetched.getElementen()) {
                }
            } catch (FetchException e) {
                e.printStackTrace();
            }
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public PollerStatus getStatus() {
        return new PollerStatus.Builder()
                .withActive(true)
                .withName(name)
                .withCurrentItem(lastProcessed)
                .withLastPoll(lastRetrieved)
                .withNbItemsRetrieved(lastProcessed)
                .withLastElementRetrievedAt(lastFetched)
                .build();
    }
}

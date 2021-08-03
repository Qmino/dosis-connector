package be.vlaio.dosis.connector.poller;

import be.vlaio.dosis.connector.common.DosisItem;
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
    private WorkInProgress wip;
    private DosisItemFactory dif;

    // Permanent items
    private final String baseUrl;
    private final String name;
    private final DossierbeheersysteemFetcher itemFetcher;
    // Current status
    private boolean active;
    private long lastProcessed;
    private int nbItemsRetrieved;
    private LocalDateTime lastFetched;
    private LocalDateTime lastRetrieved;

    /**
     * Creates a new poller based on a pollerspecification
     * @param spec the spec containing the name and base url of the poller
     */
    public Poller(PollerSpecification spec, WorkInProgress wip, DosisItemFactory dif) {
        this.baseUrl = spec.getUrl();
        this.name = spec.getName();
        this.itemFetcher = new DossierbeheersysteemFetcher(baseUrl);
        this.wip = wip;
        this.dif = dif;
        this.lastProcessed = wip.getLastIndexProcessed(this.name);
    }

    /**
     * Hoofd-loop van de poller: fetched nieuwe elementen bij het dossierbeheersysteem en voegt deze toe aan de WIP.
     * Zolang er elementen aanwezig waren in de call, blijft de poller calls doen. Vanaf het werk is afgelopen,
     * zal de methode stoppen met de opgelegde delay.
     */
    @Scheduled(fixedDelayString = "${dosisgateway.poller.delay}")
    public void fetchItems() {
        if (active && wip.readyToAcceptNewWork()) {
            try {
                lastFetched = LocalDateTime.now();
                DossierStatusCollectionTO fetched = itemFetcher.fetchItems(lastProcessed, 100);
                nbItemsRetrieved += fetched.getElementen().size();
                lastRetrieved = LocalDateTime.now();
                for (DossierStatusTO item: fetched.getElementen()) {
                    DosisItem element = dif.from(item);
                    wip.addNewDosisItem(element, name, item.getIndex());
                    lastProcessed = item.getIndex();
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
                .withNbItemsRetrieved(nbItemsRetrieved)
                .withLastElementRetrievedAt(lastFetched)
                .build();
    }
}

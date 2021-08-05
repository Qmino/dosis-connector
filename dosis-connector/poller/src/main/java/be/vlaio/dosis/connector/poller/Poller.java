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
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Lazy
@Component
public class Poller {

    private static final Logger LOGGER = LoggerFactory.getLogger(Poller.class);
    private final WorkInProgress wip;
    private final DosisItemFactory dif;

    // Permanent items
    private final PollerSpecification spec;

    private final DossierbeheersysteemFetcher itemFetcher;
    // Current status
    private boolean active;
    private long lastProcessed;
    private int consecutiveErrors = 0;
    private int skips = 0;
    private int nbItemsRetrievedSinceStart;
    private LocalDateTime lastFetched;
    private LocalDateTime lastRetrieved;
    private String lastResponse;

    /**
     * Creates a new poller based on a pollerspecification
     * @param spec the spec containing the name and base url of the poller
     */
    public Poller(PollerSpecification spec, WorkInProgress wip, DosisItemFactory dif) {
        this.spec = spec;
        this.itemFetcher = new DossierbeheersysteemFetcher(spec.getUrl());
        this.wip = wip;
        this.dif = dif;
        this.lastProcessed = wip.getLastIndexProcessed(spec.getName());
        this.active = true;
        info("Opgestart");
    }

    /**
     * Hoofd-loop van de poller: fetched nieuwe elementen bij het dossierbeheersysteem en voegt deze toe aan de WIP.
     * Zolang er elementen aanwezig waren in de call, blijft de poller calls doen. Vanaf het werk is afgelopen,
     * zal de methode stoppen met de opgelegde delay.
     */
    @Scheduled(fixedDelayString = "${dosisgateway.poller.delay}")
    public void fetchItems() {
        boolean runAgain = true;
        while (active && runAgain && wip.readyToAcceptNewWork()
                && skips >= spec.getBackoffBase() * Math.pow(consecutiveErrors, spec.getBackoffExponent())
        ) {
            try {
                skips = 0;
                lastFetched = LocalDateTime.now();
                DossierStatusCollectionTO fetched = itemFetcher.fetchItems(
                        lastProcessed+1, spec.getItemLimit());
                consecutiveErrors = 0;
                int nbElemsRetrieved = fetched.getElementen().size();
                nbItemsRetrievedSinceStart += nbElemsRetrieved;
                lastResponse = nbElemsRetrieved + " succesvol opgehaald.";
                if (nbElemsRetrieved > 0) {
                    info(lastResponse);
                } else {
                    debug(lastResponse);
                }
                for (DossierStatusTO item: fetched.getElementen()) {
                    DosisItem element = dif.from(item);
                    wip.addNewDosisItem(element, spec.getName(), item.getIndex());
                    lastProcessed = item.getIndex();
                }
                if (nbElemsRetrieved > 0) {
                    lastRetrieved = LocalDateTime.now();
                } else {
                    runAgain = false;
                }
            } catch (FetchException e) {
                lastResponse = "Fout bij ophalen elementen van dossierbeheersysteem: " + e.getMessage();
                info(lastResponse);
                consecutiveErrors++;
                if (consecutiveErrors > spec.getBackoffMaxRetries()) {
                    info("Permanente deactivatie wegens teveel fouten. Heractivatie manueel mogelijk.");
                    active = false;
                }
            }
        }
        if (consecutiveErrors > 0) {
            skips++;
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private void info(String s) {
        LOGGER.info("Poller " + spec.getName() + ": " + s);
    }

    private void debug(String s) {
        LOGGER.debug("Poller " + spec.getName() + ": " + s);
    }

    public PollerStatus getStatus() {
        return new PollerStatus.Builder()
                .withActive(active)
                .withName(spec.getName())
                .withCurrentItem(lastProcessed)
                .withLastPoll(lastFetched)
                .withNbItemsRetrieved(nbItemsRetrievedSinceStart)
                .withLastElementRetrievedAt(lastRetrieved)
                .withLastResponse(lastResponse)
                .build();
    }

    public boolean isActive() {
        return active;
    }

    public void resetBackoff() {
        this.lastResponse = null;
        this.skips = 0;
        this.consecutiveErrors = 0;
    }
}

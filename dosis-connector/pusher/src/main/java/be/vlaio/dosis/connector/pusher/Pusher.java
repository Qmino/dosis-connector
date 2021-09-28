package be.vlaio.dosis.connector.pusher;


import be.vlaio.dosis.connector.common.dosisdomain.DosisItem;
import be.vlaio.dosis.connector.common.operational.ServiceError;
import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import be.vlaio.dosis.connector.pusher.dosis.DosisClient;
import be.vlaio.dosis.connector.pusher.dosis.DosisClientException;
import be.vlaio.dosis.connector.pusher.dosis.DosisTOFactory;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierUploadStatusTO;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class Pusher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pusher.class);
    private final WorkInProgress wip;
    private final DosisClient client;
    private DosisTOFactory dosisTOFactory;

    // Values for exponential backoff
    private int consecutiveErrors = 0;
    private int skips = 0;
    @Value("${dosisgateway.dosis.backoffBase}")
    private int backoffBase = 10;
    @Value("${dosisgateway.dosis.backoffExponent}")
    private int backoffExponent = 3;
    @Value("${dosisgateway.dosis.backoffMaxRetries}")
    private int backoffMaxRetries = 10;


    // Current status
    private boolean active;
    private LocalDateTime lastDosisInteraction;
    private String lastResponse;

    /**
     * Constructor voor een nieuwe pusher
     *
     * @param client      de dosis webclient die zal gebruikt worden voor interactie met DOSIS
     * @param wip         de work in progress component die de items die moeten verstuurd worden naar dosis aanlevert
     * @param itemFactory de factory die zal gebruikt worden om dosis dtos aan te maken
     */
    public Pusher(DosisClient client, WorkInProgress wip, DosisTOFactory itemFactory) {
        this.dosisTOFactory = itemFactory;
        this.client = client;
        this.wip = wip;
        this.active = true;
        LOGGER.info("DosisPusher Opgestart");
    }

    /**
     * Hoofd-loop van de pusher: fetched nieuwe elementen bij het dossierbeheersysteem en voegt deze toe aan de WIP.
     * Zolang er elementen aanwezig waren in de call, blijft de poller calls doen. Vanaf het werk is afgelopen,
     * zal de methode stoppen met de opgelegde delay. Exponential backoff wordt gebruikt in geval er fouten zijn bij
     * het ophalen van de de gegevens bij het dossiersysteem.
     */
    @Scheduled(fixedDelayString = "${dosisgateway.dosis.delay}")
    public void sendItems() {
        Optional<DosisItem> itemInState = wip.getItemInState(Verwerkingsstatus.TODO);
        while (active && itemInState.isPresent() &&
                skips >= backoffBase * Math.pow(consecutiveErrors, backoffExponent)) {
            try {
                DosisItem item = itemInState.get();
                skips = 0;
                lastDosisInteraction = LocalDateTime.now();
                DosisDossierUploadStatusTO result = client.laadDossierStatusOp(dosisTOFactory.from(item));
                consecutiveErrors = 0;
                lastResponse = "Dossier met nummer " + item.getDossierNummer() + " opgeladen met uploadId "
                        + result.getUploadId() + ". Dosis response status was: " + result.getStatus();
                LOGGER.debug(lastResponse);
                wip.transitionItem(item, Verwerkingsstatus.UNVALIDATED);
            } catch (DosisClientException e) {
                // Something went wrong.
                // If there is an authentication error, we abort and put the server in status inactive
                if (e.getServiceError() == ServiceError.AUTHENTICATION_ERROR) {
                    LOGGER.info("Probleem bij authenticatie met Dosis: " + e.getMessage()
                            + "Client is gedeactiveerd. Manuele heractivatie is mogelijk, doch voor een aanpassing van " +
                            " de DOSIS configuratie is een herstart vereist.", e);
                    active = false;
                }
                // If there is a client issue, we immediately transition the element to an error state
                if (e.getServiceError() == ServiceError.CLIENT_ERROR) {
                    Map<String, String> props = new HashMap<>();
                    props.put("foutBoodschap", e.getMessage());
                    wip.transitionItem(itemInState.get(), Verwerkingsstatus.FAILED, props);
                } else if (e.getServiceError() == ServiceError.SERVER_ERROR || e.getServiceError() == ServiceError.TIMEOUT) {
                    lastResponse = "Fout bij doorsturen naar dosis van element " + itemInState.get().getDossierNummer() + ":" + e.getMessage();
                    LOGGER.info(lastResponse);
                    consecutiveErrors++;
                    if (consecutiveErrors > backoffMaxRetries) {
                        LOGGER.info("Permanente deactivatie wegens teveel fouten. Heractivatie manueel mogelijk.");
                        active = false;
                    }
                }
            }
            itemInState = wip.getItemInState(Verwerkingsstatus.TODO);
        }
        if (consecutiveErrors > 0) {
            skips++;
        }
    }


    public void setActive(boolean active) {
        this.active = active;
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

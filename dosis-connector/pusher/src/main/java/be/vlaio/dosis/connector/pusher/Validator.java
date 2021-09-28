package be.vlaio.dosis.connector.pusher;


import be.vlaio.dosis.connector.common.dosisdomain.DosisItem;
import be.vlaio.dosis.connector.common.operational.PusherValidatorStatus;
import be.vlaio.dosis.connector.common.operational.ServiceError;
import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import be.vlaio.dosis.connector.pusher.dosis.DosisClient;
import be.vlaio.dosis.connector.pusher.dosis.DosisClientException;
import be.vlaio.dosis.connector.pusher.dosis.DosisTOFactory;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierUploadValidatieTO;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisVerwerkingFoutTO;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
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
    private LocalDateTime lastCallAttempt;
    private LocalDateTime lastCallExecuted;
    private String lastResponse;

    /**
     * Constructor voor een nieuwe pusher
     *
     * @param client      de dosis webclient die zal gebruikt worden voor interactie met DOSIS
     * @param wip         de work in progress component die de items die moeten verstuurd worden naar dosis aanlevert
     * @param itemFactory de factory die zal gebruikt worden om dosis dtos aan te maken
     */
    public Validator(DosisClient client, WorkInProgress wip, DosisTOFactory itemFactory) {
        this.dosisTOFactory = itemFactory;
        this.client = client;
        this.wip = wip;
        this.active = true;
        LOGGER.info("DosisValidator Opgestart");
    }

    /**
     * Hoofd-loop van de validator: haalt elementen op van de wip in status unvalidated, controleert of het element
     * goedgekeurd of afgekeurd is, en verwerkt zo het element. Exponential backoff wordt gebruikt in geval er
     * fouten zijn bij connectie met DOSIS (bv internal server erros of timeouts).
     */
    @Scheduled(fixedDelayString = "${dosisgateway.dosis.delay}")
    public void sendItems() {
        Optional<DosisItem> itemInState = wip.getItemInState(Verwerkingsstatus.UNVALIDATED);
        while (active && itemInState.isPresent() &&
                skips >= backoffBase * Math.pow(consecutiveErrors, backoffExponent)) {
            try {
                DosisItem item = itemInState.get();
                skips = 0;
                lastCallAttempt = LocalDateTime.now();
                DosisDossierUploadValidatieTO result = client.getValidatieDossierStatusOp(item.getId());
                lastCallExecuted = LocalDateTime.now();
                consecutiveErrors = 0;
                lastResponse = "Validatie van dossier " + item.getDossierNummer() + " (uploadId "
                        + result.getUploadId() + "). Dosis response status was: [Success: " + result.isSuccess()
                        + ", verwerkt: " + result.getVerwerktVoorOpvraging() + "]";
                LOGGER.debug(lastResponse);
                Map<String, String> attributen = new HashMap<>();
                attributen.put("Tijdstipverwerking", result.getTijdstipVerwerking().format(DateTimeFormatter.ISO_DATE_TIME));
                attributen.put("Success", "" + result.isSuccess());
                if (result.isSuccess()) {
                    wip.transitionItem(item, Verwerkingsstatus.COMPLETED, attributen);
                } else {
                    String fouten = result.getFout() + " [" +
                            result.getFouten().stream()
                                    .map(DosisVerwerkingFoutTO::toString)
                                    .collect(Collectors.joining(" / ")) +
                            "]";

                    attributen.put("Fouten", fouten);
                    wip.transitionItem(item, Verwerkingsstatus.FAILED, attributen);
                }
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
            itemInState = wip.getItemInState(Verwerkingsstatus.UNVALIDATED);
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

    public PusherValidatorStatus getStatus() {
        return new PusherValidatorStatus.Builder()
                .withNumberOfConsecutiveErrors(consecutiveErrors)
                .withLastResult(lastResponse)
                .withLastCallAttempt(lastCallAttempt)
                .withLastCallExecuted(lastCallExecuted)
                .withActive(isActive())
                .build();
    }
}

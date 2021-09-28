package be.vlaio.dosis.connector.poller;

import be.vlaio.dosis.connector.common.dosisdomain.*;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Factory die op basis van een DossierStatusCollectionTO een reeks DosisItems maakt.
 */
@Component
public class DosisItemFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DosisItemFactory.class);

    /**
     * Maakt een lijst van dosisitems op basis van de dossierstatuscollection die werd teruggegeven door het
     * dossierbeheersysteem.
     *
     * @param collectionTO De respons van het dossierbeheersysteem
     * @return Een lijst met dosisitems.
     */
    public List<DosisItem> from(DossierStatusCollectionTO collectionTO) {
        List<DosisItem> items = new ArrayList<>();
        if (collectionTO == null || collectionTO.getElementen() == null || collectionTO.getElementen().size() == 0) {
            return items;
        } else {
            return collectionTO.getElementen().stream().map(this::from).collect(Collectors.toList());
        }
    }

    /**
     * Maakt een DosisItem vertrekkende van een DossierStatusTO. Indien de gegeven statusto null is, zal ook
     * null worden teruggegeven.
     * @param status de status waarvan vertrokken wordt
     * @return een DosisItem
     */
    public DosisItem from(DossierStatusTO status) {
        return status == null ? null : new DosisItem.Builder()
                .withId(UUID.randomUUID())
                .withStatus(from(status.getStatus()))
                .withProduct(status.getProduct())
                .withWijzigingsDatum(status.getWijzigingsdatum())
                .withDossierBeheerder(from(status.getDossierBeheerder()))
                .withDossierNummer(status.getDossiernummer())
                .withDossierNaam(status.getDossiernaam())
                .withDoorverwijzingsUrl(status.getDoorverwijzingUrl())
                .withAgenten(status.getAgenten() == null
                        ? null
                        : status.getAgenten().stream().map(this::from).collect(Collectors.toList()))
                .build();
    }

    /**
     * Zet een AgentTO om naar een Agent. Indien de gegeven agentTo null is, zal ook null worden teruggegeven.
     * @param agent de agent vanwaar vertrokken wordt
     * @return een Agent
     */
    public Agent from(AgentTO agent) {
        Agent result = null;
        if (agent != null) {
            if (agent instanceof BurgerAgentTO) {
                BurgerAgentTO burger = (BurgerAgentTO) agent;
                result = new BurgerAgent.Builder().withRijksregisterNummer(burger.getRijksregisternummer()).build();
            } else if (agent instanceof OndernemerAgentTO) {
                OndernemerAgentTO ondernemer = (OndernemerAgentTO) agent;
                result = new OndernemerAgent.Builder()
                        .withKboNummer(ondernemer.getKboNummer())
                        .withToegangsRechten(ondernemer.getToegangsrechten() == null ? null :
                                ondernemer.getToegangsrechten().stream().map(this::from).collect(Collectors.toList()))
                        .build();
            } else {
                LOGGER.error("Ongekend agent type kan niet omgezet worden voor verwerking.");
            }
        }
        return result;
    }

    /**
     * Geeft een ToegangsRecht op basis van een toegangsRechtTO. Indien de gegeven to null is, zal ook null worden
     * teruggegeven.
     * @param toegangsRechtTO de to vanwaar vertrokken wordt
     * @return een toegangsrecht dat overeenkomt met de gegeven to.
     */
    public ToegangsRecht from(ToegangsRechtTO toegangsRechtTO) {
        if (toegangsRechtTO == null) {
            return null;
        } else {
            return new ToegangsRecht.Builder()
                    .withRecht(toegangsRechtTO.getRecht())
                    .withContext(toegangsRechtTO.getContext())
                    .build();
        }
    }

    /**
     * Zet een contactTO om naar een contact. Indien de gegeven contactto null is, zal ook null worden teruggegeven.
     * @param contact de contactto waarvan wordt vertrokken.
     * @return een omgezet Contact object
     */
    public Contact from(ContactTO contact) {
        Contact result = null;
        if (contact != null) {
            return new Contact.Builder()
                    .withDienst(contact.getDienst())
                    .withWebsite(contact.getWebsite())
                    .withTelefoon(contact.getTelefoon())
                    .withEmail(contact.getEmail())
                    .withNaam(contact.getNaam())
                    .withAdres(
                            contact.getAdres() == null ? null :
                                    new Adres.Builder()
                                            .withGemeente(contact.getAdres().getGemeente())
                                            .withStraat(contact.getAdres().getStraat())
                                            .withHuisNummer(contact.getAdres().getHuisnummer())
                                            .withPostCode(contact.getAdres().getPostcode())
                                            .build()
                    ).build();
        }
        return result;
    }

    /**
     * Zet een statusTo om naar een DossierStatus. Indien de gegeven statusto null is, zal ook null worden teruggegeven.
     * @param status de to waarvan vertrokken wordt.
     * @return een omgezetten DossierStatus
     */
    public DossierStatus from(StatusTO status) {
        DossierStatus result = null;
        if (status != null) {
            return new DossierStatus.Builder()
                    .withVlaamseFase(status.getVlaamseFase())
                    .withVlaamseCode(status.getVlaamseCode())
                    .withActie(status.getActie())
                    .withDetail(status.getDetail())
                    .build();
        }
        return result;
    }
}

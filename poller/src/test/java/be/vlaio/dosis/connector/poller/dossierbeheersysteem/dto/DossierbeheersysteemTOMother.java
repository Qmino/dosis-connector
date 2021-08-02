package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import be.vlaio.dosis.connector.common.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Testklasse die responses genereert voor wiremock om pollers te kunnen testen.
 */
public class DossierbeheersysteemTOMother {

    private static int counter = 0;
    private static Random random = new Random();
    private static String doorverwijzingUrl = "http://eloket-test.vlaio.be/nl/eloket/mijnloket/test/";
    private static String dossierbeheerSysteemUrl = "http://dossierstatusevoa.ovam.be/geefStatusVeranderingen";
    private static ObjectMapper mapper = CommonTestMother.objectMapper();
    private static final String[] vlaamseFasen = new String[] {"Samenstelling", "Behandeling", "Beslissing", "Beroep", "Uitvoering", "Afgerond"};
    private static final String[][] vlaamseCoden = new String[][] {
            {"Geinitieerd", "Aangevraagd", "Ingediend"},
            {"DossierOnvolledig", "DossierVolledig", "Ontvankelijk", "OnOntvankelijk", "InBehandeling", "KlaarVoorBeslissing"},
            {"InWacht", "Beslist","Goedgekeurd", "Stopgezet", "Stopzetting","Geweigerd"},
            {"InWacht", "Beslist","Goedgekeurd", "Stopgezet", "Stopzetting","Geweigerd"},
            {"KlaarVoorBetaling","Betaald", "DeelsUitbetaald","Uitgevoerd"},
            {"ErkendVergund","DeelsGoedgekeurd"}};

    public static String someDossierStatusCollectionTO(int index, int limiet, int aantalElementen)  {
        int getal = counter++;
        List<DossierStatusTO> dossierStatussen = new ArrayList<>();
        for (int i = 0; i < aantalElementen; i++) {
            dossierStatussen.add(new DossierStatusTO.Builder()
                    .withDossiernaam("Testdossier " + getal)
                    .withDossiernummer("TEST-" + UUID.randomUUID())
                    .withDoorverwijzingUrl(doorverwijzingUrl + getal)
                    .withStatus(someStatus().build())
                    .withProduct(random.nextInt(100))
                    .withDossierBeheerder(someContact().build())
                    .withAgenten(CommonTestMother.randomList(DossierbeheersysteemTOMother::someAgent))
                    .withWijzigingsdatum(LocalDateTime.now().minusDays(random.nextInt(50)))
                    .build());
        }

        try {
            return mapper.writeValueAsString(new DossierStatusCollectionTO.Builder()
                        .withElementen(dossierStatussen)
                        .withId(dossierbeheerSysteemUrl+"?index=" + index + "&limiet=" + limiet)
                        .withVolgendeVerzameling(dossierbeheerSysteemUrl+"?index=" + (index+aantalElementen) + "&limiet=" + limiet)
                        .withLimiet(limiet)
                        .withNieuweIndex(index+aantalElementen)
                        .withType("StatusVeranderingsVerzameling")
                        .build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String someValidationError() {
        return "{\n" +
                "\t\"@type\": \"../errortype/1\",\n" +
                "\t\"status\": 412,\n" +
                "\t\"title\": \"Er waren validatie fouten\",\n" +
                "\t\"errors\": [\n" +
                "\t \t{ \n" +
                "\t \t\"@type\": \"../errortype/2\",\n" +
                "\t \t\"title\": \"De waarde is geen geldige waarde.\",\n" +
                "\t \t\"detail\": \"De waarde moet tussen 0 en 100 liggen\"\n" +
                "\t \t}\n" +
                "\t]\n" +
                "}";
    }


    private static StatusTO.Builder someStatus() {
        int fase = random.nextInt(6);
        return new StatusTO.Builder()
                .withActie("Actie nr " + random.nextInt(10))
                .withDetail("Detail nr " + random.nextInt(10))
                .withVlaamseCode(CommonTestMother.random(vlaamseCoden[fase]))
                .withVlaamseFase(CommonTestMother.random(vlaamseFasen));
    }

    private static ContactTO.Builder someContact() {
        return new ContactTO.Builder()
                .withAdres(someAdres().build())
                .withEmail(CommonTestMother.randomEmail())
                .withTelefoon("+3249" + RandomStringUtils.randomNumeric(7))
                .withWebsite("www." + CommonTestMother.randomDomein())
                .withDienst("Dienst " + RandomStringUtils.randomNumeric(1));
    }

    private static AdresTO.Builder someAdres() {
        return new AdresTO.Builder()
                .withGemeente(CommonTestMother.randomStad())
                .withPostcode(RandomStringUtils.randomNumeric(4))
                .withStraat(CommonTestMother.randomStraat())
                .withHuisnummer(RandomStringUtils.randomNumeric(2));
    }

    private static AgentTO someAgent() {
        if (random.nextBoolean()) {
            return someBurgerAgent().build();
        } else {
            return someOndernemerAgent().build();
        }
    }

    private static BurgerAgentTO.Builder someBurgerAgent() {
        return new BurgerAgentTO.Builder()
                .withRijksregisternummer(RandomStringUtils.randomNumeric(11));
    }

    private static OndernemerAgentTO.Builder someOndernemerAgent() {
        return new OndernemerAgentTO.Builder()
                .withKboNummer("BE" + RandomStringUtils.randomNumeric(9))
                .withToegangsrechten(CommonTestMother.random(random.nextInt(4), () -> someToegangsRecht().build()));
    }

    private static ToegangsRechtTO.Builder someToegangsRecht() {
        return new ToegangsRechtTO.Builder()
                .withRecht("Recht nr " + random.nextInt(10))
                .withContext("Context nr " + random.nextInt(100));
    }


}

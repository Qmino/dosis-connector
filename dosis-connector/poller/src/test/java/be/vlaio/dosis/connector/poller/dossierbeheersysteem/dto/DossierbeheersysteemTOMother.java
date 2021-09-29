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
        counter++;
        List<DossierStatusTO> dossierStatussen = new ArrayList<>();
        for (int i = 0; i < aantalElementen; i++) {
            dossierStatussen.add(someDossierStatus().withIndex(index + i).build());
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

    public static DossierStatusTO.Builder someDossierStatus() {
        return new DossierStatusTO.Builder()
                .withDossiernaam("Testdossier " + counter)
                .withDossiernummer("TEST-" + UUID.randomUUID())
                .withDoorverwijzingUrl(doorverwijzingUrl + counter)
                .withStatus(someStatus().build())
                .withIndex(counter)
                .withProduct(random.nextInt(100))
                .withDossierBeheerder(someContact().build())
                .withAgenten(CommonTestMother.randomList(DossierbeheersysteemTOMother::someAgent))
                .withWijzigingsdatum(LocalDateTime.now().minusDays(random.nextInt(50)));
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


    public static StatusTO.Builder someStatus() {
        int fase = random.nextInt(6);
        return new StatusTO.Builder()
                .withActie("Actie nr " + random.nextInt(10))
                .withDetail("Detail nr " + random.nextInt(10))
                .withVlaamseCode(CommonTestMother.random(vlaamseCoden[fase]))
                .withVlaamseFase(CommonTestMother.random(vlaamseFasen));
    }

    public static ContactTO.Builder someContact() {
        return new ContactTO.Builder()
                .withNaam(CommonTestMother.randomPersoonNaam() + " Test")
                .withAdres(someAdres().build())
                .withEmail(CommonTestMother.randomEmail())
                .withTelefoon("+3249" + RandomStringUtils.randomNumeric(7))
                .withWebsite("http://www." + CommonTestMother.randomDomein())
                .withDienst("Dienst " + RandomStringUtils.randomNumeric(1));
    }

    public static AdresTO.Builder someAdres() {
        return new AdresTO.Builder()
                .withGemeente(CommonTestMother.randomStad())
                .withPostcode(RandomStringUtils.randomNumeric(4))
                .withStraat(CommonTestMother.randomStraat())
                .withHuisnummer(RandomStringUtils.randomNumeric(2));
    }

    public static AgentTO someAgent() {
        if (random.nextBoolean()) {
            return someBurgerAgent().build();
        } else {
            return someOndernemerAgent().build();
        }
    }

    public static BurgerAgentTO.Builder someBurgerAgent() {

        return new BurgerAgentTO.Builder()
                .withRijksregisternummer(randomRR());
    }

    public static OndernemerAgentTO.Builder someOndernemerAgent() {
        return new OndernemerAgentTO.Builder()
                .withKboNummer("0" + RandomStringUtils.randomNumeric(9))
                .withToegangsrechten(CommonTestMother.random(random.nextInt(4), () -> someToegangsRecht().build()));
    }

    public static ToegangsRechtTO.Builder someToegangsRecht() {
        return new ToegangsRechtTO.Builder()
                .withRecht("Recht nr " + random.nextInt(10))
                .withContext("Context nr " + random.nextInt(100));
    }


    public static String randomRR() {
        int year = random.nextInt(100);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        String rr = year + (month < 10 ? "0" + month : "" + month) + (day < 10 ? "0" + day : "" + day);
        long seq = random.nextInt(1000);
        if (seq < 10) {
            rr += "0";
        }
        if (seq < 100) {
            rr += "0";
        }
        rr += seq;
        long total = Long.parseLong(rr);
        long modulo = total % 97;
        if (modulo < 10) {
            rr += "0";
        }
        rr += modulo;
        return rr;
    }
}

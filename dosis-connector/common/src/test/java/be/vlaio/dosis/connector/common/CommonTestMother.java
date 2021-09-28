package be.vlaio.dosis.connector.common;

import be.vlaio.dosis.connector.common.dosisdomain.*;
import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

/**
 * Klasse om snel test data te genereren voor unittesten. Publieke methodes van deze klasse geven builders terug ipv
 * de reeds gebouwde objecten, zodat deze makkelijk in testen kunnen gewijzigd worden.
 */
public class CommonTestMother {

    private static final String[] steden = new String[] {"Brugge", "Antwerpen", "Gent", "Mechelen", "Lokeren", "Brussel"};
    private static final String[] straten = new String[] {"Bosplein", "Bondgenotenlaan", "Groenstraat", "Verzonnenlaan"};
    private static final String[] personen = new String[] {"Jan", "Pieter", "Korneel", "Karel", "Karolien"};
    private static final String[] domeinen = new String[] {"gmail.com", "vlaanderen.be", "hotmail.com", "outlook.com"};
    private static final String[] vlaamseFasen = new String[] {"Samenstelling", "Behandeling", "Beslissing", "Beroep", "Uitvoering", "Afgerond"};
    private static final String[][] vlaamseCoden = new String[][] {
            {"Geinitieerd", "Aangevraagd", "Ingediend"},
            {"DossierOnvolledig", "DossierVolledig", "Ontvankelijk", "OnOntvankelijk", "InBehandeling", "KlaarVoorBeslissing"},
            {"InWacht", "Beslist","Goedgekeurd", "Stopgezet", "Stopzetting","Geweigerd"},
            {"InWacht", "Beslist","Goedgekeurd", "Stopgezet", "Stopzetting","Geweigerd"},
            {"KlaarVoorBetaling","Betaald", "DeelsUitbetaald","Uitgevoerd"},
            {"ErkendVergund","DeelsGoedgekeurd"}};

    private static final Random r = new Random();

    public static Adres.Builder someAdres() {
        return new Adres.Builder()
                .withGemeente(random(steden))
                .withPostCode(RandomStringUtils.randomNumeric(4))
                .withStraat(random(straten))
                .withHuisNummer(RandomStringUtils.randomNumeric(2));
    }

    private static Agent someAgent() {
        if (r.nextBoolean()) {
            return someBurgerAgent().build();
        } else {
            return someOndernemerAgent().build();
        }
    }

    public static BurgerAgent.Builder someBurgerAgent() {
        return new BurgerAgent.Builder()
                .withRijksregisterNummer(RandomStringUtils.randomNumeric(11));
    }

    public static OndernemerAgent.Builder someOndernemerAgent() {
        return new OndernemerAgent.Builder()
                .withKboNummer("BE" + RandomStringUtils.randomNumeric(9))
                .withToegangsRechten(random(r.nextInt(4), () -> someToegangsRecht().build()));
    }

    public static ToegangsRecht.Builder someToegangsRecht() {
        return new ToegangsRecht.Builder()
                .withRecht("Recht nr " + r.nextInt(10))
                .withContext("Context nr " + r.nextInt(100));
   }

   public static Contact.Builder someContact() {
        return new Contact.Builder()
                .withAdres(someAdres().build())
                .withEmail(randomEmail())
                .withTelefoon("+3249" + RandomStringUtils.randomNumeric(7))
                .withWebsite("www." + randomDomein())
                .withDienst("Dienst " + RandomStringUtils.randomNumeric(1));
   }

   public static String randomPersoonNaam() {
       return random(personen);
   }

   public static String randomDomein() {
        return random(domeinen);
   }

   public static String randomEmail() {
        return randomPersoonNaam() + "@" + randomDomein();
   }

   public static String randomStad() {
        return random(steden);
   }

   public static String randomStraat() {
        return random(straten);
   }

   public static DossierStatus.Builder someDossierStatus() {
        int fase = r.nextInt(6);
        return new DossierStatus.Builder()
                .withActie("Actie nr " + r.nextInt(10))
                .withDetail("Detail nr " + r.nextInt(10))
                .withVlaamseCode(random(vlaamseCoden[fase]))
                .withVlaamseFase(random(vlaamseFasen));
   }

   public static Verwerkingsstatus someVerwerkingsstatus() {
        return random(Verwerkingsstatus.values());
   }

   public static DosisItem.Builder someDosisItem() {
        return new DosisItem.Builder()
                .withAgenten(random(2, () -> someAgent()))
                .withDoorverwijzingsUrl("https://some.url.from." + random(domeinen) + "/with/some/id/" + RandomStringUtils.randomNumeric(4))
                .withDossierNaam("Random dossiernaam " + r.nextInt(10))
                .withDossierNummer(RandomStringUtils.randomAlphanumeric(10))
                .withDossierBeheerder(someContact().build())
                .withId(UUID.randomUUID())
                .withProduct(r.nextInt(10))
                .withWijzigingsDatum(LocalDateTime.now().minusDays(r.nextInt(50)))
                .withStatus(someDossierStatus().build());
   }

    public static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
    }

    public static <T> T random(T[] input) {
        return input[r.nextInt(input.length)];
    }

    public static <T> List<T> randomList(Supplier<T> generator) {
        return random(r.nextInt(5)+1, generator);
    }

    public static <T> List<T> random(int size, Supplier<T> generator) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(generator.get());
        }
        return result;
    }
}

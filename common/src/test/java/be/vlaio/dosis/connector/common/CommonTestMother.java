package be.vlaio.dosis.connector.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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
                .withEmail(random(personen) + "@" + random(domeinen))
                .withTelefoon("+3249" + RandomStringUtils.randomNumeric(7))
                .withWebsite("www." + random(domeinen))
                .withDienst("Dienst " + RandomStringUtils.randomNumeric(1));
   }

   public static DossierStatus.Builder someDossierStatus() {
        return new DossierStatus.Builder()
                .withActie("Actie nr " + r.nextInt(10))
                .withDetail("Detail nr " + r.nextInt(10))
                .withVlaamseCode("Vlaamse Code " + r.nextInt(10))
                .withVlaamseFase("Vlaamse fase " + r.nextInt(10));
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

    private static <T> T random(T[] input) {
        return input[r.nextInt(input.length)];
    }

    private static <T> List<T> randomList(Supplier<T> generator) {
        return random(r.nextInt(5), generator);
    }

    private static <T> List<T> random(int size, Supplier<T> generator) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(generator.get());
        }
        return result;
    }
}

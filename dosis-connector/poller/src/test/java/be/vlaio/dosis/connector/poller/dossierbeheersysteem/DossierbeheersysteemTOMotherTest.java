package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class DossierbeheersysteemTOMotherTest {


    @Test
    public void checkRRValid() {
        String resultaat = DossierbeheersysteemTOMother.randomRR();
        Assertions.assertEquals(11, resultaat.length());
        long first = Long.parseLong(resultaat.substring(0, 9));
        long mod = Long.parseLong(resultaat.substring(9, 11));
        Assertions.assertEquals(mod, first % 97);
    }
}

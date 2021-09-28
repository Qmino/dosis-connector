package be.vlaio.dosis.connector.common;

import be.vlaio.dosis.connector.common.dosisdomain.DossierStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DossierStatusTest {

    @Test
    @DisplayName("Jackson configuratie setup")
    public void deserializationTest() {
        String value = "{\n" +
                "\t\"vlaamseCode\": \"Een vlaamse Code\",\n" +
                "\t\"vlaamseFase\": \"Een vlaamse fase\",\n" +
                "\t\"detail\": \"Een detail\",\n" +
                "\t\"actie\": \"Een actie\",\n" +
                "\t\"someUnknownField\": \"Should be ignored\"\n" +
                "}";
        try {
            DossierStatus status = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(value, DossierStatus.class);
            assertEquals("Een vlaamse Code", status.getVlaamseCode());
            assertEquals("Een vlaamse fase", status.getVlaamseFase());
            assertEquals("Een detail", status.getDetail());
            assertEquals("Een actie", status.getActie());
        } catch (JsonProcessingException e) {
            fail("Kon niet deserializeren");
        }
    }

}

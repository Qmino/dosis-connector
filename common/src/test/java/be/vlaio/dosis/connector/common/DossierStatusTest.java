package be.vlaio.dosis.connector.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class DossierStatusTest {

    @Test
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
            Assert.assertEquals("Een vlaamse Code", status.getVlaamseCode());
            Assert.assertEquals("Een vlaamse fase", status.getVlaamseFase());
            Assert.assertEquals("Een detail", status.getDetail());
            Assert.assertEquals("Een actie", status.getActie());
        } catch (JsonProcessingException e) {
            Assert.fail("Kon niet deserializeren");
        }
    }

}

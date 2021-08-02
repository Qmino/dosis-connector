package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import be.vlaio.dosis.connector.common.CommonTestMother;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.JsonExpectationsHelper;

public class DossierStatusCollectionTOTest {

    @Test
    public void deserializeTest() {
        ObjectMapper mapper = CommonTestMother.objectMapper();
        // Testcase uit de open-api specificatie gehaald.
        String testJson = "{\n" +
                "  \"@type\": \"StatusVeranderingsVerzameling\",\n" +
                "  \"@id\": \"http://dossierstatusevoa.ovam.be/geefStatusVeranderingen?index=1223372036854775807&limiet=25\",\n" +
                "  \"index\": 1223372036854775800,\n" +
                "  \"limiet\": 25,\n" +
                "  \"nieuweIndex\": 1223372036854775800,\n" +
                "  \"@volgendeVerzameling\": \"http://dossierstatusevoa.ovam.be/geefStatusVeranderingen?index=1223372036854775809&limiet=25\",\n" +
                "  \"elementen\": [\n" +
                "    {\n" +
                "      \"@type\": \"DossierStatus\",\n" +
                "      \"dossiernummer\": \"ASTAD-2021-177839\",\n" +
                "      \"dossiernaam\": \"Melden nieuwe zaak & aanvraag ondernemingsvergunning\",\n" +
                "      \"doorverwijzingUrl\": \"https://eloket-a.antwerpen.be/nl/eloket/mijnloket/aanvraag/608927617b5f7704350e3c3b?type=company\",\n" +
                "      \"wijzigingsdatum\": \"2021-04-28T11:16:50.52\",\n" +
                "      \"status\": {\n" +
                "        \"@type\": \"Status\",\n" +
                "        \"vlaamseFase\": \"Afgerond\",\n" +
                "        \"vlaamseCode\": \"Goedgekeurd\",\n" +
                "        \"detail\": null,\n" +
                "        \"actie\": null\n" +
                "      },\n" +
                "      \"dossier beheerder\": {\n" +
                "        \"@type\": \"Contact\",\n" +
                "        \"naam\": \"Jan Janssens\",\n" +
                "        \"dienst\": null,\n" +
                "        \"telefoon\": null,\n" +
                "        \"email\": null,\n" +
                "        \"website\": null,\n" +
                "        \"adres\": {\n" +
                "          \"@type\": \"Adres\",\n" +
                "          \"straat\": \"Wetstraat\",\n" +
                "          \"huisnummer\": \"10\",\n" +
                "          \"postcode\": \"1000\",\n" +
                "          \"gemeente\": \"Brussel\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"agenten\": [\n" +
                "        {\n" +
                "          \"@type\": \"OndernemerAgent\",\n" +
                "          \"kboNummer\": \"0848270242\",\n" +
                "          \"toegangsrechten\": [\n" +
                "            {\n" +
                "              \"recht\": \"AntwerpsGebruikersbeheer\",\n" +
                "              \"context\": \"eloketgebruiker\"\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"@type\": \"BurgerAgent\",\n" +
                "          \"rijksregisternummer\": \"string\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"product\": 200\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            // Deze test is gewoon om te kijken of we alle velden wel degelijk deserializen. Serialization is niet
            // vereist in deze applicatie, aangezezien we gewoon de API gebruiken.
            DossierStatusCollectionTO parsed = mapper.readValue(testJson, DossierStatusCollectionTO.class);
            new JsonExpectationsHelper().assertJsonEqual(testJson, mapper.writeValueAsString(parsed), true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

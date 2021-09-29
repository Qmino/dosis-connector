package be.vlaio.dosis.connector;

import be.vlaio.dosis.connector.common.CommonTestMother;
import be.vlaio.dosis.connector.pusher.dosis.DosisClient;
import be.vlaio.dosis.connector.pusher.dosis.DosisClientException;
import be.vlaio.dosis.connector.pusher.dosis.DosisConstanten;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierTO;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = {WireMockInitializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DosisClientTest {

    @Autowired
    private DosisClient dosisClient;
    @Autowired
    private WireMockServer wireMockServer;

    @Value("${dosisgateway.dosis.bronUri}")
    private String bron;

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    @Order(1)
    @DisplayName("Need for refresh access token correctly determined")
    public void testRefreshAccessToken() throws DosisClientException {
        Assertions.assertTrue(dosisClient.shouldRefreshAccessToken());
        int expiresIn = DosisConstanten.REFRESH_MARGIN + 1;
        this.wireMockServer.stubFor(
                WireMock.post(urlPathEqualTo("/authorization"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "    \"access_token\":\"MOB1ZkyrqdSay4-xm39Z6A==\",\n" +
                                        "    \"token_type\":\"bearer\",\n" +
                                        "    \"expires_in\":" + expiresIn + "\n" +
                                        "}"))
        );
        dosisClient.refreshAccessToken();
        Assertions.assertFalse(dosisClient.shouldRefreshAccessToken());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(dosisClient.shouldRefreshAccessToken());
    }

    @Test
    @Order(2)
    @DisplayName("Accesstoken used correctly")
    public void testAccessTokenUse() throws DosisClientException {
        Assertions.assertTrue(dosisClient.shouldRefreshAccessToken());
        int expiresIn = DosisConstanten.REFRESH_MARGIN + 1;
        this.wireMockServer.stubFor(
                WireMock.post(urlPathEqualTo("/authorization"))
                        .inScenario("AccesstokenTest")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "    \"access_token\":\"SomeFirstToken\",\n" +
                                        "    \"token_type\":\"bearer\",\n" +
                                        "    \"expires_in\":" + expiresIn + "\n" +
                                        "}"))
                        .willSetStateTo("SecondToken")
        );
        this.wireMockServer.stubFor(
                WireMock.post(urlPathEqualTo("/authorization"))
                        .inScenario("AccesstokenTest")
                        .whenScenarioStateIs("SecondToken")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "    \"access_token\":\"SomeSecondToken\",\n" +
                                        "    \"token_type\":\"bearer\",\n" +
                                        "    \"expires_in\":" + 500 + "\n" +
                                        "}"))
        );
        this.wireMockServer.stubFor(
                WireMock.post(urlPathEqualTo("/base" + DosisConstanten.DOSSIER_STATUS_UPLOAD_URL))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\n" +
                                        "  \"Status\": \"OK\",\n" +
                                        "  \"UploadId\": \"c30c0de7-f763-4648-b684-936162aa6a78\"\n" +
                                        "}"))
        );

        dosisClient.laadDossierStatusOp(
                new DosisDossierTO.Builder().from(
                        CommonTestMother.someDosisItem().build(), bron).build());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dosisClient.laadDossierStatusOp(
                new DosisDossierTO.Builder().from(
                        CommonTestMother.someDosisItem().build(), bron).build());

        this.wireMockServer.verify(
                exactly(2),
                postRequestedFor(urlEqualTo("/authorization")));


        this.wireMockServer.verify(
                exactly(1),
                postRequestedFor(
                        urlEqualTo("/base" + DosisConstanten.DOSSIER_STATUS_UPLOAD_URL))
                        .withHeader("Authorization", equalTo("Bearer SomeFirstToken")));
        this.wireMockServer.verify(
                exactly(1),
                postRequestedFor(
                        urlEqualTo("/base" + DosisConstanten.DOSSIER_STATUS_UPLOAD_URL))
                        .withHeader("Authorization", equalTo("Bearer SomeSecondToken")));
    }



}

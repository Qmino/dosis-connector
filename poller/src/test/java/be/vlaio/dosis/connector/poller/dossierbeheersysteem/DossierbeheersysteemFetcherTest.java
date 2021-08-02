package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.ServiceError;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class DossierbeheersysteemFetcherTest {

    @Autowired
    private WireMockServer wireMockServer;

    private DossierbeheersysteemFetcher fetcher;

    @BeforeEach()
    public void before() {
        fetcher = new DossierbeheersysteemFetcher("http://localhost:9090");
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    @DisplayName("Basis fetch scenario")
    void testGetStatusveranderingen() {
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(DossierbeheersysteemTOMother.someDossierStatusCollectionTO(0,25,10))));
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.assertTrue(result.getElementen().size() <= 25);
            Assertions.assertEquals(0, result.getIndex());
            Assertions.assertEquals(25, result.getLimiet());
        } catch (FetchException e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("Timeout gedrag statusverandering fetcher")
    void testGetStatusveranderingenTimouts() {
        fetcher.setTimeOut(200);
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(DossierbeheersysteemTOMother.someDossierStatusCollectionTO(0,25,10))
                                .withFixedDelay(5000))
        );
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.fail("Timeout niet gerespecteerd!");
        } catch (FetchException e) {
            // Ok.
            Assertions.assertEquals(ServiceError.TIMEOUT, e.getErrorType());
        }
    }

    @Test
    @DisplayName("Onverwachtte payload dossierbeheersysteem")
    void testReactieOnverwachtePayload() {
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"id\":500}"))
        );
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.fail("Exception verwacht");
        } catch (FetchException e) {
            // Ok.
            Assertions.assertEquals(ServiceError.UNEXPECTED_CONTENT_ERROR, e.getErrorType());
        }
    }

    @Test
    @DisplayName("Client Error")
    void testReactieClientError() {
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(412)
                                .withBody(DossierbeheersysteemTOMother.someValidationError()))
        );
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.fail("Exception verwacht");
        } catch (FetchException e) {
            // Ok.
            Assertions.assertEquals(ServiceError.CLIENT_ERROR, e.getErrorType());
        }
    }

    @Test
    @DisplayName("Server Error")
    void testReactieServerError() {
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(500)
                                .withBody("Internal server errror"))
        );
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.fail("Exception verwacht");
        } catch (FetchException e) {
            // Ok.
            Assertions.assertEquals(ServiceError.SERVER_ERROR, e.getErrorType());
        }
    }

    @Test
    @DisplayName("Server Error")
    void testReactieConnectionError() {
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(301)
                        ));
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            Assertions.fail("Exception verwacht");
        } catch (FetchException e) {
            // Ok.
            Assertions.assertEquals(ServiceError.UNKNOWN, e.getErrorType());
        }
    }
}

package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class DossierbeheersysteemFetcherTest {

    @Autowired
    private WireMockServer wireMockServer;

    private DossierbeheersysteemFetcher fetcher;

    @LocalServerPort
    private Integer port;

    @BeforeEach()
    public void before() {
        fetcher = new DossierbeheersysteemFetcher("http://localhost:" + port);
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    void testGetStatusveranderingen() {
        this.wireMockServer.stubFor(
                WireMock.get("/dossierstatusveranderingen")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(DossierbeheersysteemTOMother.someDossierStatusCollectionTO(0,25,10))));
        try {
            DossierStatusCollectionTO result = fetcher.fetchItems(0, 25);
            System.out.println("Hehe");
        } catch (FetchException e) {
            Assertions.fail();
        }
    }

}

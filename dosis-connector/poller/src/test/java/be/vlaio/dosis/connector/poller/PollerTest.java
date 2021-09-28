package be.vlaio.dosis.connector.poller;

import be.vlaio.dosis.connector.common.operational.PollerSpecification;
import be.vlaio.dosis.connector.common.operational.PollerStatus;
import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.WireMockInitializer;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import be.vlaio.dosis.connector.wip.DiskStore;
import be.vlaio.dosis.connector.wip.WorkInProgress;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.time.LocalDateTime;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"dosisgateway.poller.delay=200"})
@ContextConfiguration(initializers = {WireMockInitializer.class})
public class PollerTest {

    @TempDir
    File rootFolder;
    @Autowired
    private WireMockServer wireMockServer;
    private WorkInProgress wip;

    @Autowired
    private Function<PollerSpecification, BiFunction<WorkInProgress, DosisItemFactory, Poller>> pollerFactory;

    private Poller poller;

    @BeforeEach()
    public void before() {
        PollerSpecification ps = new PollerSpecification(
                "testpoller",
                "http://localhost:9090",
                2,
                10,
                3,
                3);
        DiskStore store = new DiskStore(rootFolder.getAbsolutePath(),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .registerModule(new JavaTimeModule()));
        wip = new WorkInProgress(2, 5, store);
        poller = pollerFactory.apply(ps).apply(wip, new DosisItemFactory());
    }

    @AfterEach
    public void afterEach() {
        this.wireMockServer.resetAll();
    }

    @Test
    @DisplayName("Basis fetch scenario")
    void testGetStatusveranderingen() {
        // Eerste call: 2 elementen
        this.wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                        .inScenario("PollerTest")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(DossierbeheersysteemTOMother.someDossierStatusCollectionTO(0, 2, 2)))
                        .willSetStateTo("Step0")
        );

        // tweede call: 2 elementen, derde, vierde en vijfde call: niets, 6e call en 7e call: 2 elementen
        for (int i = 0; i < 6; i++) {
            this.wireMockServer.stubFor(
                    WireMock.get(urlPathEqualTo("/dossierstatusveranderingen"))
                            .inScenario("PollerTest")
                            .whenScenarioStateIs("Step" + i)
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                    .withBody(DossierbeheersysteemTOMother.someDossierStatusCollectionTO(
                                            2 + i * 2, 2, (i == 0 || i > 3) ? 2 : 0)))
                            .willSetStateTo("Step" + (i + 1))
            );
        }
        // We testen niet het schedule gedrag van spring, enkel het gedrag van de poller!
        assertEquals(0, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        poller.setActive(false);
        poller.fetchItems(); // doet niets, want inactive.
        assertEquals(0, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        PollerStatus status = poller.getStatus();
        assertFalse(status.isActive());
        assertEquals("testpoller", status.getName());
        assertEquals(-1, status.getCurrentItem());
        Assertions.assertNull(status.getLastElementRetrievedAt());
        Assertions.assertNull(status.getLastPoll());
        assertEquals(0, status.getNbItemsRetrieved());
        poller.setActive(true);
        status = poller.getStatus();
        Assertions.assertTrue(status.isActive());
        assertEquals("testpoller", status.getName());
        assertEquals(-1, status.getCurrentItem());
        Assertions.assertNull(status.getLastElementRetrievedAt());
        Assertions.assertNull(status.getLastPoll());
        assertEquals(0, status.getNbItemsRetrieved());

        LocalDateTime before = LocalDateTime.now();
        poller.fetchItems(); // eerste en tweede call, samen 4 items, derde call niets en stopt daar.
        LocalDateTime after = LocalDateTime.now();
        assertEquals(4, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        status = poller.getStatus();
        Assertions.assertTrue(status.isActive());
        assertEquals("testpoller", status.getName());
        assertEquals(3, status.getCurrentItem());
        assertEquals(4, status.getNbItemsRetrieved());
        Assertions.assertTrue(status.getLastElementRetrievedAt().isAfter(before));
        Assertions.assertTrue(status.getLastElementRetrievedAt().isBefore(after));
        Assertions.assertTrue(status.getLastPoll().isAfter(before) || status.getLastPoll().isEqual(before));
        Assertions.assertTrue(status.getLastPoll().isBefore(after) || status.getLastPoll().isEqual(after));

        LocalDateTime elementRetrievedAt = status.getLastElementRetrievedAt();
        LocalDateTime newBefore = LocalDateTime.now();
        poller.fetchItems(); // vierde call: 0 items
        LocalDateTime newAfter = LocalDateTime.now();
        assertEquals(4, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        status = poller.getStatus();
        Assertions.assertTrue(status.isActive());
        assertEquals("testpoller", status.getName());
        assertEquals(3, status.getCurrentItem());
        assertEquals(4, status.getNbItemsRetrieved());
        Assertions.assertEquals(elementRetrievedAt, status.getLastElementRetrievedAt());
        Assertions.assertTrue(status.getLastPoll().isAfter(newBefore) || status.getLastPoll().isEqual(newBefore));
        Assertions.assertTrue(status.getLastPoll().isBefore(newAfter) || status.getLastPoll().isEqual(newAfter));

        poller.fetchItems(); // vijfde call: 0 items
        assertEquals(4, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        poller.fetchItems(); // zesde call: 2 items
        assertEquals(6, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
        poller.fetchItems(); // zevende call: 2 items, maar wip volzet, dus niet toevoegen.
        assertEquals(6, wip.getStatus().getItems().get(Verwerkingsstatus.TODO));
    }
}

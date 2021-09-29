package be.vlaio.dbssim;

import be.vlaio.dosis.connector.common.CommonTestMother;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.AgentTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class DbSimService {

    @Value("${generate.hardcoded.product: -1}")
    private int product = -1;

    @Value("${generate.hardcoded.rijksregisterNummer: }")
    private String rijksregisterNummer;

    @Value("${generate.hardcoded.ondernemingsNummer: }")
    private String ondernemingsNummer;


    @Value("${generate.itemsPerCycle: 3}")
    private int itemsPerCycle;

    @Value("${generate.maxItems: 6000}")
    private int maxItems;

    @Value("${generate.errorPercentage: 5}")
    private int errorPercentage;

    private int counter;
    private Random random = new Random();
    private List<DossierStatusTO> list = new ArrayList<>();

    /**
     * This generates the items to be served.
     */
    @Scheduled(fixedDelayString = "${generate.delay}")
    public void update() {
        if (list.size() < maxItems) {
            for (int i = 0; i < itemsPerCycle; i++) {
                counter++;
                list.add(someDossierStatusTO());
            }
        }
    }

    @GetMapping(value = "dossierstatusveranderingen", produces = "application/json")
    @ResponseBody
    public DossierStatusCollectionTO getStatus(
            @RequestParam("index") int index,
            @RequestParam("limiet") int limiet) {
        int nieuweIndex;
        List<DossierStatusTO> elementen;
        if (index > list.size()) {
            nieuweIndex = list.size();
            elementen = new ArrayList<>();
        } else {
            int from = Math.max(0, index);
            int to = Math.min(list.size(), index + limiet);
            elementen = list.subList(from, to);
            nieuweIndex = to;
        }
        return new DossierStatusCollectionTO.Builder()
                .withType("StatusVeranderingsVerzameling")
                .withNieuweIndex(nieuweIndex)
                .withLimiet(limiet)
                .withIndex(index)
                .withVolgendeVerzameling("Doesn't matter")
                .withElementen(elementen)
                .withId(UUID.randomUUID().toString()).build();
    }

    @GetMapping(value = "fout/dossierstatusveranderingen", produces = "application/json")
    @ResponseBody
    public DossierStatusCollectionTO getErrorStatus(
            @RequestParam("index") int index,
            @RequestParam("limiet") int limiet) {
        throw new NullPointerException("Internal server error triggeren :)");
    }


    private DossierStatusTO someDossierStatusTO() {
        DossierStatusTO.Builder value = DossierbeheersysteemTOMother.someDossierStatus()
                .withIndex(counter);
        if (product != -1) {
            value.withProduct(product);
        }

        int randomValue = random.nextInt(100);
        if (randomValue<errorPercentage) {
            List<AgentTO> invalidAgenten = new ArrayList<>();
            invalidAgenten.add(DossierbeheersysteemTOMother.someBurgerAgent().withRijksregisternummer("123456").build());
            value.withAgenten(invalidAgenten);
        } else {
            if (rijksregisterNummer.length() > 0 || ondernemingsNummer.length() > 0) {
                List<AgentTO> agenten = new ArrayList<>();
                if (rijksregisterNummer.length() > 0) {
                    agenten.add(DossierbeheersysteemTOMother.someBurgerAgent().withRijksregisternummer(rijksregisterNummer).build());
                }
                if (ondernemingsNummer.length() > 0) {
                    agenten.add(DossierbeheersysteemTOMother.someOndernemerAgent()
                            .withKboNummer(ondernemingsNummer)
                            .withToegangsrechten(CommonTestMother.random(random.nextInt(2) + 1, () -> {
                                return DossierbeheersysteemTOMother.someToegangsRecht().build();
                            })).build());
                }
                value.withAgenten(agenten);
            }
        }
        return value.build();
    }

}

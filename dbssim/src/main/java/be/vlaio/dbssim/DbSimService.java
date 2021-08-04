package be.vlaio.dbssim;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierbeheersysteemTOMother;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class DbSimService {

    private int counter;
    private Random random = new Random();
    private List<DossierStatusTO> list = new ArrayList<>();

    @Scheduled(fixedDelayString = "1000")
    public void update() {
        if (list.size() < 5000) {
            for (int i = 0; i < 3; i++) {
                counter++;
                list.add(someDossierStatusTO());
            }
        }
    }

    @GetMapping(value="dossierstatusveranderingen", produces="application/json")
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
            int to = Math.min(list.size()-1, index+limiet);
            elementen = list.subList(index, to);
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



    private DossierStatusTO someDossierStatusTO() {
        return DossierbeheersysteemTOMother.someDossierStatus()
                .withIndex(counter)
                .build();
    }

}

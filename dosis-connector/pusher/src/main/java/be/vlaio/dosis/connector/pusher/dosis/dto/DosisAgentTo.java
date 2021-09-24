package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.Agent;
import be.vlaio.dosis.connector.common.BurgerAgent;
import be.vlaio.dosis.connector.common.OndernemerAgent;
import be.vlaio.dosis.connector.common.ToegangsRecht;
import be.vlaio.dosis.connector.pusher.dosis.DosisConstanten;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DosisAgentTo {

    @JsonProperty("Identificatie")
    private String identificatie;

    @JsonProperty("Toegangsrechten")
    private List<DosisToegangsRechtTO> toegangsrechten;

    public DosisAgentTo(String identificatie, List<DosisToegangsRechtTO> toegangsrechten) {
        this.identificatie = identificatie;
        this.toegangsrechten = toegangsrechten;
    }

    public DosisAgentTo(Agent agent) {
        this.toegangsrechten = new ArrayList<>();
        if (agent instanceof OndernemerAgent) {
            OndernemerAgent a = (OndernemerAgent) agent;
            this.identificatie = a.getKboNummer();
            for (ToegangsRecht recht: a.getToegangsRechten()) {
                this.toegangsrechten.add(new DosisToegangsRechtTO(recht.getRecht()));
            }
        } else if (agent instanceof BurgerAgent) {
            BurgerAgent a = (BurgerAgent) agent;
            this.identificatie = a.getRijksregisterNummer();
            this.toegangsrechten.add(new DosisToegangsRechtTO(DosisConstanten.BURGERRECHT));
        }
    }

    public String getIdentificatie() {
        return identificatie;
    }

    public List<DosisToegangsRechtTO> getToegangsrechten() {
        return toegangsrechten;
    }


}

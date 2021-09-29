package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.dosisdomain.Agent;
import be.vlaio.dosis.connector.common.dosisdomain.BurgerAgent;
import be.vlaio.dosis.connector.common.dosisdomain.OndernemerAgent;
import be.vlaio.dosis.connector.common.dosisdomain.ToegangsRecht;
import be.vlaio.dosis.connector.pusher.dosis.DosisConstanten;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DosisAgentTO {

    @JsonProperty("Identificatie")
    private String identificatie;

    @JsonProperty("Toegangsrechten")
    private List<DosisToegangsRechtTO> toegangsrechten;

    public DosisAgentTO(String identificatie, List<DosisToegangsRechtTO> toegangsrechten) {
        this.identificatie = identificatie;
        this.toegangsrechten = toegangsrechten;
    }

    public DosisAgentTO(Agent agent) {
        this.toegangsrechten = new ArrayList<>();
        if (agent instanceof OndernemerAgent) {
            OndernemerAgent a = (OndernemerAgent) agent;
            this.identificatie = a.getKboNummer();
            for (ToegangsRecht recht: a.getToegangsRechten()) {
                String toegangsRecht = recht.getRecht();
                if (recht.getContext() != null && recht.getContext().length() > 0) {
                    toegangsRecht += " - " + recht.getContext();
                }
                this.toegangsrechten.add(new DosisToegangsRechtTO(toegangsRecht));
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

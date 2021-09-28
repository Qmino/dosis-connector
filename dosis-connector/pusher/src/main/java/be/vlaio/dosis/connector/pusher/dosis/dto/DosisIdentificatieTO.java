package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisIdentificatieTO {


    private final String bron;

    private final String dossierNummer;

    public String getBron() {
        return bron;
    }

    public String getDossierNummer() {
        return dossierNummer;
    }

    public DosisIdentificatieTO(@JsonProperty("Bron") String bron, @JsonProperty("DossierNummer") String dossierNummer) {
        this.bron = bron;
        this.dossierNummer = dossierNummer;
    }


}

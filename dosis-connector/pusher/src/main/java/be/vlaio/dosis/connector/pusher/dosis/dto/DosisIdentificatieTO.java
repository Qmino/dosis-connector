package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisIdentificatieTO {

    @JsonProperty("Bron")
    private final String bron;
    @JsonProperty("DossierNummer")
    private final String dossierNummer;

    public String getBron() {
        return bron;
    }

    public String getDossierNummer() {
        return dossierNummer;
    }

    public DosisIdentificatieTO(String bron, String dossierNummer) {
        this.bron = bron;
        this.dossierNummer = dossierNummer;
    }


}

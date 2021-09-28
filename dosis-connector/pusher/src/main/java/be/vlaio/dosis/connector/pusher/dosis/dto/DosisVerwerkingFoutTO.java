package be.vlaio.dosis.connector.pusher.dosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DosisVerwerkingFoutTO {

    private String omschrijving;
    private Integer lijnNummer;

    public DosisVerwerkingFoutTO(@JsonProperty("Omschrijving") String omschrijving,
                                 @JsonProperty("LijnNummer") Integer lijnNummer) {
        this.omschrijving = omschrijving;
        this.lijnNummer = lijnNummer;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public Integer getLijnNummer() {
        return lijnNummer;
    }

    public String toString() {
        return (lijnNummer == null ? "" : lijnNummer + ": ") + omschrijving;
    }
}

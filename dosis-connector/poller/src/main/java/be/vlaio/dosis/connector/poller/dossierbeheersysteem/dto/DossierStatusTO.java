package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.List;

@JsonDeserialize(builder = DossierStatusTO.Builder.class)
public class DossierStatusTO {

    @JsonProperty("@type")
    private final String type;
    private final String dossiernummer;
    private final String dossiernaam;
    private final String doorverwijzingUrl;
    private final LocalDateTime wijzigingsdatum;
    private final StatusTO status;
    @JsonProperty("dossier beheerder")
    private final ContactTO dossierBeheerder;
    private final List<AgentTO> agenten;
    private final int product;
    private final long index;

    public long getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }

    public String getDossiernummer() {
        return dossiernummer;
    }

    public String getDossiernaam() {
        return dossiernaam;
    }

    public String getDoorverwijzingUrl() {
        return doorverwijzingUrl;
    }

    public LocalDateTime getWijzigingsdatum() {
        return wijzigingsdatum;
    }

    public StatusTO getStatus() {
        return status;
    }

    public ContactTO getDossierBeheerder() {
        return dossierBeheerder;
    }

    public List<AgentTO> getAgenten() {
        return agenten;
    }

    public int getProduct() {
        return product;
    }

    public DossierStatusTO(String type, String dossiernummer, String dossiernaam, String doorverwijzingUrl,
                           LocalDateTime wijzigingsdatum, StatusTO status, ContactTO dossierBeheerder,
                           List<AgentTO> agenten, int product, long index) {
        this.type = type;
        this.dossiernummer = dossiernummer;
        this.dossiernaam = dossiernaam;
        this.doorverwijzingUrl = doorverwijzingUrl;
        this.wijzigingsdatum = wijzigingsdatum;
        this.status = status;
        this.dossierBeheerder = dossierBeheerder;
        this.agenten = agenten;
        this.product = product;
        this.index = index;
    }


    @JsonPOJOBuilder
    public static final class Builder {
        private String type = "DossierStatus";
        private String dossiernummer;
        private String dossiernaam;
        private String doorverwijzingUrl;
        private LocalDateTime wijzigingsdatum;
        private StatusTO status;
        private ContactTO dossierBeheerder;
        private List<AgentTO> agenten;
        private int product;
        private long index;

        public Builder() {
        }

        @JsonSetter("@type")
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withDossiernummer(String dossiernummer) {
            this.dossiernummer = dossiernummer;
            return this;
        }

        public Builder withDossiernaam(String dossiernaam) {
            this.dossiernaam = dossiernaam;
            return this;
        }

        public Builder withDoorverwijzingUrl(String doorverwijzingUrl) {
            this.doorverwijzingUrl = doorverwijzingUrl;
            return this;
        }

        public Builder withWijzigingsdatum(LocalDateTime wijzigingsdatum) {
            this.wijzigingsdatum = wijzigingsdatum;
            return this;
        }

        public Builder withStatus(StatusTO status) {
            this.status = status;
            return this;
        }

        @JsonSetter("dossier beheerder")
        public Builder withDossierBeheerder(ContactTO dossierBeheerder) {
            this.dossierBeheerder = dossierBeheerder;
            return this;
        }

        public Builder withAgenten(List<AgentTO> agenten) {
            this.agenten = agenten;
            return this;
        }

        public Builder withIndex(long index) {
            this.index = index;
            return this;
        }

        public Builder withProduct(int product) {
            this.product = product;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withType(type)
                    .withIndex(index)
                    .withDossiernummer(dossiernummer)
                    .withDossiernaam(dossiernaam)
                    .withDoorverwijzingUrl(doorverwijzingUrl)
                    .withWijzigingsdatum(wijzigingsdatum)
                    .withStatus(status)
                    .withDossierBeheerder(dossierBeheerder)
                    .withAgenten(agenten)
                    .withProduct(product);
        }

        public DossierStatusTO build() {
            return new DossierStatusTO(type, dossiernummer, dossiernaam, doorverwijzingUrl, wijzigingsdatum,
                    status, dossierBeheerder, agenten, product, index);
        }
    }
}

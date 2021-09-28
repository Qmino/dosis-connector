package be.vlaio.dosis.connector.common.dosisdomain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonDeserialize(builder=DosisItem.Builder.class)
public class DosisItem {

    private final UUID id;
    private final LocalDateTime wijzigingsDatum;
    private final String dossierNummer;
    private final String dossierNaam;
    private final String doorverwijzingsUrl;
    private final int product;
    private final List<Agent> agenten;
    private final DossierStatus status;
    private final Contact dossierBeheerder;

    private DosisItem(UUID id, LocalDateTime wijzigingsDatum,
                     String dossierNummer, String dossierNaam, String doorverwijzingsUrl,
                     int product, List<Agent> agenten, DossierStatus status, Contact dossierBeheerder) {
        this.id = id;
        this.wijzigingsDatum = wijzigingsDatum;
        this.dossierNummer = dossierNummer;
        this.dossierNaam = dossierNaam;
        this.doorverwijzingsUrl = doorverwijzingsUrl;
        this.product = product;
        this.agenten = agenten;
        this.status = status;
        this.dossierBeheerder = dossierBeheerder;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getWijzigingsDatum() {
        return wijzigingsDatum;
    }

    public String getDossierNummer() {
        return dossierNummer;
    }

    public String getDossierNaam() {
        return dossierNaam;
    }

    public String getDoorverwijzingsUrl() {
        return doorverwijzingsUrl;
    }

    public int getProduct() {
        return product;
    }

    public List<Agent> getAgenten() {
        return agenten;
    }

    public DossierStatus getStatus() {
        return status;
    }

    public Contact getDossierBeheerder() {
        return dossierBeheerder;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private UUID id;
        private LocalDateTime wijzigingsDatum;
        private String dossierNummer;
        private String dossierNaam;
        private String doorverwijzingsUrl;
        private int product;
        private List<Agent> agenten;
        private DossierStatus status;
        private Contact dossierBeheerder;

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withWijzigingsDatum(LocalDateTime wijzigingsDatum) {
            this.wijzigingsDatum = wijzigingsDatum;
            return this;
        }

        public Builder withDossierNummer(String dossierNummer) {
            this.dossierNummer = dossierNummer;
            return this;
        }

        public Builder withDossierNaam(String dossierNaam) {
            this.dossierNaam = dossierNaam;
            return this;
        }

        public Builder withDoorverwijzingsUrl(String doorverwijzingsUrl) {
            this.doorverwijzingsUrl = doorverwijzingsUrl;
            return this;
        }

        public Builder withProduct(int product) {
            this.product = product;
            return this;
        }

        public Builder withAgenten(List<Agent> agenten) {
            this.agenten = agenten;
            return this;
        }

        public Builder withStatus(DossierStatus status) {
            this.status = status;
            return this;
        }

        public Builder withDossierBeheerder(Contact dossierBeheerder) {
            this.dossierBeheerder = dossierBeheerder;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withId(id)
                    .withWijzigingsDatum(wijzigingsDatum)
                    .withDossierNummer(dossierNummer)
                    .withDossierNaam(dossierNaam)
                    .withDoorverwijzingsUrl(doorverwijzingsUrl)
                    .withProduct(product)
                    .withAgenten(agenten)
                    .withStatus(status)
                    .withDossierBeheerder(dossierBeheerder);
        }

        public DosisItem build() {
            return new DosisItem(id, wijzigingsDatum, dossierNummer, dossierNaam,
                    doorverwijzingsUrl, product, agenten, status, dossierBeheerder);
        }
    }
}

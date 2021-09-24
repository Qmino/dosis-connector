package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.Agent;
import be.vlaio.dosis.connector.common.DosisItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payload voor communicatie met Dosis API.
 */
public class DosisDossierTO {

    @JsonProperty("Identificatie")
    private DosisIdentificatieTO identificatie;
    @JsonProperty("UploadId")
    private UUID uploadId;
    @JsonProperty("Naam")
    private String naam;
    @JsonProperty("WijzigingsDatum")
    private LocalDateTime wijzigingsDatum;
    @JsonProperty("Status")
    private DosisDossierStatusTo status;
    @JsonProperty("TypeDossierCode")
    private String typeDossierCode = "DossierStatus"; // Voor onze doelstellingen hardcoded

    // In de API is een lijst voorzien, maar hier moet altijd een enkel element in. Het was "designed" voor latere
    // uitbreidingen die niet nodig blijken te zijn. Toekomstige versies van dosis gaan hier een enkel veld van maken.
    // Vandaar dat we in de builder hier een enkel veld van maken.
    @JsonProperty("Producten")
    private List<ProductIdTo> producten;

    @JsonProperty("DossierBeheerder")
    private DosisDossierContactTO dossierBeheerder;

    @JsonProperty("Agenten")
    private List<DosisAgentTo> agenten;

    public DosisDossierTO(DosisIdentificatieTO identificatie,
                          UUID uploadId,
                          String naam,
                          LocalDateTime wijzigingsDatum,
                          DosisDossierStatusTo status,
                          List<ProductIdTo> producten,
                          DosisDossierContactTO dossierBeheerder,
                          List<DosisAgentTo> agenten) {
        this.identificatie = identificatie;
        this.uploadId = uploadId;
        this.naam = naam;
        this.wijzigingsDatum = wijzigingsDatum;
        this.status = status;
        this.producten = producten;
        this.dossierBeheerder = dossierBeheerder;
        this.agenten = agenten;
    }

    public DosisIdentificatieTO getIdentificatie() {
        return identificatie;
    }

    public UUID getUploadId() {
        return uploadId;
    }

    public String getNaam() {
        return naam;
    }

    public LocalDateTime getWijzigingsDatum() {
        return wijzigingsDatum;
    }

    public DosisDossierStatusTo getStatus() {
        return status;
    }

    public String getTypeDossierCode() {
        return typeDossierCode;
    }

    public List<ProductIdTo> getProducten() {
        return producten;
    }

    public DosisDossierContactTO getDossierBeheerder() {
        return dossierBeheerder;
    }

    public List<DosisAgentTo> getAgenten() {
        return agenten;
    }

    public static final class Builder {
        private DosisIdentificatieTO identificatie;
        private UUID uploadId;
        private String naam;
        private LocalDateTime wijzigingsDatum;
        private DosisDossierStatusTo status;
        private List<ProductIdTo> producten;
        private DosisDossierContactTO dossierBeheerder;
        private List<DosisAgentTo> agenten;

        public Builder() {
        }

        public Builder withIdentificatie(DosisIdentificatieTO identificatie) {
            this.identificatie = identificatie;
            return this;
        }

        public Builder withUploadId(UUID uploadId) {
            this.uploadId = uploadId;
            return this;
        }

        public Builder withNaam(String naam) {
            this.naam = naam;
            return this;
        }

        public Builder withWijzigingsDatum(LocalDateTime wijzigingsDatum) {
            this.wijzigingsDatum = wijzigingsDatum;
            return this;
        }

        public Builder withStatus(DosisDossierStatusTo status) {
            this.status = status;
            return this;
        }

        public Builder withProduct(ProductIdTo product) {
            if (product != null) {
                this.producten = new ArrayList<>();
                this.producten.add(product);
            }
            return this;
        }

        public Builder withDossierBeheerder(DosisDossierContactTO dossierBeheerder) {
            this.dossierBeheerder = dossierBeheerder;
            return this;
        }

        public Builder withAgenten(List<DosisAgentTo> agenten) {
            this.agenten = agenten;
            return this;
        }

        public Builder from(DosisItem item, String bron) {
            this.naam = item.getDossierNaam();
            this.identificatie = new DosisIdentificatieTO(bron, item.getDossierNummer());
            this.uploadId = UUID.randomUUID();
            this.wijzigingsDatum = item.getWijzigingsDatum();
            this.status = item.getStatus() == null ? null : new DosisDossierStatusTo.Builder().from(item.getStatus()).build();
            withProduct(new ProductIdTo("" + item.getProduct()));
            this.dossierBeheerder = item.getDossierBeheerder() == null ? null :  new DosisDossierContactTO.Builder().from(item.getDossierBeheerder()).build();
            this.agenten = item.getAgenten() == null
                    ? null
                    : item.getAgenten().stream().map(DosisAgentTo::new).collect(Collectors.toList());
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withIdentificatie(identificatie)
                    .withUploadId(uploadId)
                    .withNaam(naam)
                    .withWijzigingsDatum(wijzigingsDatum)
                    .withStatus(status)
                    .withProduct(producten == null || producten.size() == 0 ? null : producten.get(0))
                    .withDossierBeheerder(dossierBeheerder).withAgenten(agenten);
        }

        public DosisDossierTO build() {
            return new DosisDossierTO(identificatie, uploadId, naam, wijzigingsDatum, status, producten, dossierBeheerder, agenten);
        }
    }
}

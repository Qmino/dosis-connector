package be.vlaio.dosis.connector.pusher.dosis.dto;

import be.vlaio.dosis.connector.common.Contact;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * Payload voor een contact richting de dosis API.
 */
@JsonDeserialize(builder = DosisDossierContactTO.Builder.class)
public class DosisDossierContactTO {

    @JsonProperty("Naam")
    private String naam;
    @JsonProperty("Dienst")
    private final String dienst;
    @JsonProperty("Telefoon")
    private final String telefoon;
    @JsonProperty("Email")
    private final String email;
    @JsonProperty("Website")
    private final String website;
    @JsonProperty("Adres")
    private final DosisContactAdresTO adres;


    public DosisDossierContactTO(String naam, String dienst, String telefoon, String email, String website, DosisContactAdresTO adres) {
        this.naam = naam;
        this.dienst = dienst;
        this.telefoon = telefoon;
        this.email = email;
        this.website = website;
        this.adres = adres;
    }

    public String getNaam() {
        return naam;
    }

    public String getDienst() {
        return dienst;
    }

    public String getTelefoon() {
        return telefoon;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public DosisContactAdresTO getAdres() {
        return adres;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String naam;
        private String dienst;
        private String telefoon;
        private String email;
        private String website;
        private DosisContactAdresTO adres;

        public Builder() {
        }

        public Builder withNaam(String naam) {
            this.naam = naam;
            return this;
        }

        public Builder withDienst(String dienst) {
            this.dienst = dienst;
            return this;
        }

        public Builder withTelefoon(String telefoon) {
            this.telefoon = telefoon;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withWebsite(String website) {
            this.website = website;
            return this;
        }

        public Builder withAdres(DosisContactAdresTO adres) {
            this.adres = adres;
            return this;
        }

        public Builder from(Contact contact) {
            this.naam = contact.getNaam();
            this.dienst = contact.getDienst();
            this.telefoon = contact.getTelefoon();
            this.email = contact.getEmail();
            this.website = contact.getWebsite();
            this.adres = new DosisContactAdresTO.Builder().from(contact.getAdres()).build();
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withNaam(naam)
                    .withDienst(dienst)
                    .withTelefoon(telefoon)
                    .withEmail(email)
                    .withWebsite(website)
                    .withAdres(adres);
        }

        public DosisDossierContactTO build() {
            return new DosisDossierContactTO(naam, dienst, telefoon, email, website, adres);
        }
    }
}

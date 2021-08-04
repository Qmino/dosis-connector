package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = ContactTO.Builder.class)
public class ContactTO {

    @JsonProperty("@type")
    private final String type;
    private final String naam;
    private final String dienst;
    private final String telefoon;
    private final String email;
    private final String website;
    private final AdresTO adres;

    public ContactTO(String type, String naam, String dienst, String telefoon, String email, String website, AdresTO adres) {
        this.type = type;
        this.naam = naam;
        this.dienst = dienst;
        this.telefoon = telefoon;
        this.email = email;
        this.website = website;
        this.adres = adres;
    }

    public String getType() {
        return type;
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

    public AdresTO getAdres() {
        return adres;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String type;
        private String naam;
        private String dienst;
        private String telefoon;
        private String email;
        private String website;
        private AdresTO adres;

        public Builder() {
        }

        @JsonSetter("@type")
        public Builder withType(String type) {
            this.type = type;
            return this;
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

        public Builder withAdres(AdresTO adres) {
            this.adres = adres;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withType(type)
                    .withNaam(naam)
                    .withDienst(dienst)
                    .withTelefoon(telefoon)
                    .withEmail(email)
                    .withWebsite(website)
                    .withAdres(adres);
        }

        public ContactTO build() {
            return new ContactTO(type, naam, dienst, telefoon, email, website, adres);
        }
    }
}

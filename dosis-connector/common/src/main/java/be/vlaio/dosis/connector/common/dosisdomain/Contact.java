package be.vlaio.dosis.connector.common.dosisdomain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Contact.Builder.class)
public class Contact {

    private final String naam;
    private final String dienst;
    private final String telefoon;
    private final String email;
    private final String website;
    private final Adres adres;

    private Contact(String naam, String dienst, String telefoon, String email, String website, Adres adres) {
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

    public Adres getAdres() {
        return adres;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String naam;
        private String dienst;
        private String telefoon;
        private String email;
        private String website;
        private Adres adres;

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

        public Builder withAdres(Adres adres) {
            this.adres = adres;
            return this;
        }

        public Contact build() {
            return new Contact(naam, dienst, telefoon, email, website, adres);
        }
    }
}

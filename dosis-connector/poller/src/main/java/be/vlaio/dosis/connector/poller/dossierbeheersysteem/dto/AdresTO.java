package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = AdresTO.Builder.class)
public class AdresTO {

    @JsonProperty("@type")
    private final String type;
    private final String straat;
    private final String huisnummer;
    private final String postcode;
    private final String gemeente;

    public AdresTO(String type, String straat, String huisnummer, String postcode, String gemeente) {
        this.type = type;
        this.straat = straat;
        this.huisnummer = huisnummer;
        this.postcode = postcode;
        this.gemeente = gemeente;
    }

    public String getType() {
        return type;
    }

    public String getStraat() {
        return straat;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getGemeente() {
        return gemeente;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String type = "Adres";
        private String straat;
        private String huisnummer;
        private String postcode;
        private String gemeente;

        public Builder() {
        }

        @JsonSetter("@type")
        public Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Builder withStraat(String straat) {
            this.straat = straat;
            return this;
        }

        public Builder withHuisnummer(String huisnummer) {
            this.huisnummer = huisnummer;
            return this;
        }

        public Builder withPostcode(String postcode) {
            this.postcode = postcode;
            return this;
        }

        public Builder withGemeente(String gemeente) {
            this.gemeente = gemeente;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withType(type)
                    .withStraat(straat)
                    .withHuisnummer(huisnummer)
                    .withPostcode(postcode)
                    .withGemeente(gemeente);
        }

        public AdresTO build() {
            return new AdresTO(type, straat, huisnummer, postcode, gemeente);
        }
    }
}

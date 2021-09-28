package be.vlaio.dosis.connector.common.dosisdomain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = BurgerAgent.Builder.class)
public class BurgerAgent extends Agent {

    private final String rijksregisterNummer;

    public BurgerAgent(String rijksregisterNummer) {
        this.rijksregisterNummer = rijksregisterNummer;
    }

    public String getRijksregisterNummer() {
        return rijksregisterNummer;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String rijksregisterNummer;

        public Builder withRijksregisterNummer(String rijksregisterNummer) {
            this.rijksregisterNummer = rijksregisterNummer;
            return this;
        }

        public BurgerAgent build() {
            return new BurgerAgent(rijksregisterNummer);
        }
    }
}

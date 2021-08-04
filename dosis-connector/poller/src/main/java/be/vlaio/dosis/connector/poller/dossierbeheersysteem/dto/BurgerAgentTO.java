package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = BurgerAgentTO.Builder.class)
public class BurgerAgentTO extends AgentTO {

    private final String rijksregisternummer;

    public BurgerAgentTO(String rijksregisternummer) {
        this.rijksregisternummer = rijksregisternummer;
    }

    public String getRijksregisternummer() {
        return rijksregisternummer;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String rijksregisternummer;

        public Builder withRijksregisternummer(String rijksregisternummer) {
            this.rijksregisternummer = rijksregisternummer;
            return this;
        }

        public BurgerAgentTO build() {
            return new BurgerAgentTO(rijksregisternummer);
        }
    }
}

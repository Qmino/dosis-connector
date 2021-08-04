package be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ToegangsRechtTO.Builder.class)
public class ToegangsRechtTO {
    private final String recht;
    private final String context;

    private ToegangsRechtTO(String recht, String context) {
        this.recht = recht;
        this.context = context;
    }

    public String getRecht() {
        return recht;
    }

    public String getContext() {
        return context;
    }

    public static final class Builder {
        private String recht;
        private String context;

        public Builder withRecht(String recht) {
            this.recht = recht;
            return this;
        }

        public Builder withContext(String context) {
            this.context = context;
            return this;
        }

        public ToegangsRechtTO build() {
            return new ToegangsRechtTO(this.recht, this.context);
        }
    }
}

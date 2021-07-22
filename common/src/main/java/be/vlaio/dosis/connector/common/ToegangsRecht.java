package be.vlaio.dosis.connector.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ToegangsRecht.Builder.class)
public class ToegangsRecht {
    private final String recht;
    private final String context;

    private ToegangsRecht(String recht, String context) {
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

        public ToegangsRecht build() {
            return new ToegangsRecht(this.recht, this.context);
        }
    }
}

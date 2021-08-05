package be.vlaio.dosis.connector.managementapi.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = PollerSpecificationTO.Builder.class)
public class PollerSpecificationTO {

    private final String name;
    private final String url;
    private final int itemLimit;
    private final int backoffBase;
    private final int backoffExponent;
    private final int backoffMaxRetries;

    public PollerSpecificationTO(String name, String url, int itemLimit, int backoffBase,
                                 int backoffExponent, int backoffMaxRetries) {
        this.name = name;
        this.url = url;
        this.itemLimit = itemLimit;
        this.backoffBase = backoffBase;
        this.backoffExponent = backoffExponent;
        this.backoffMaxRetries = backoffMaxRetries;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public int getBackoffBase() {
        return backoffBase;
    }

    public int getBackoffExponent() {
        return backoffExponent;
    }

    public int getBackoffMaxRetries() {
        return backoffMaxRetries;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String name;
        private String url;
        private int itemLimit;
        private int backoffBase = 10;
        private int backoffExponent = 3;
        private int backoffMaxRetries = 10;

        public Builder() {
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withItemLimit(int itemLimit) {
            this.itemLimit = itemLimit;
            return this;
        }

        public Builder withBackoffBase(int backoffBase) {
            this.backoffBase = backoffBase;
            return this;
        }

        public Builder withBackoffExponent(int backoffExponent) {
            this.backoffExponent = backoffExponent;
            return this;
        }

        public Builder withBackoffMaxRetries(int backoffMaxRetries) {
            this.backoffMaxRetries = backoffMaxRetries;
            return this;
        }

        public PollerSpecificationTO build() {
            return new PollerSpecificationTO(name, url, itemLimit, backoffBase, backoffExponent, backoffMaxRetries);
        }
    }
}

package be.vlaio.dosis.connector.wip;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Persistente configuratie van een poller voor opslag op disk (vooral het laatst verwerkte element)
 */
@JsonDeserialize(builder = PollerConfig.Builder.class)
public class PollerConfig {

    private final LocalDateTime lastUpdate;
    private final long lastIndex;
    private final String pollerName;
    private final UUID lastId;

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public long getLastIndex() {
        return lastIndex;
    }

    public String getPollerName() {
        return pollerName;
    }

    public UUID getLastId() {
        return lastId;
    }

    public PollerConfig(LocalDateTime lastUpdate, long lastIndex, String pollerName, UUID lastId) {
        this.lastUpdate = lastUpdate;
        this.lastIndex = lastIndex;
        this.pollerName = pollerName;
        this.lastId = lastId;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private LocalDateTime lastUpdate;
        private long lastIndex;
        private String pollerName;
        private UUID lastId;

        public Builder() {
        }

        public Builder withLastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder withLastIndex(long lastIndex) {
            this.lastIndex = lastIndex;
            return this;
        }

        public Builder withPollerName(String pollerName) {
            this.pollerName = pollerName;
            return this;
        }

        public Builder withLastId(UUID lastId) {
            this.lastId = lastId;
            return this;
        }

        public PollerConfig build() {
            return new PollerConfig(lastUpdate, lastIndex, pollerName, lastId);
        }
    }
}

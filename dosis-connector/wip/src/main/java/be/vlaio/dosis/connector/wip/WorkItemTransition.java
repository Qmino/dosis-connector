package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.operational.Verwerkingsstatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Een interne structuur die gegevens over een statustransitie van een dosisitem bijhoudt.
 */
@JsonDeserialize(builder = WorkItemTransition.Builder.class)
class WorkItemTransition {

    private final Verwerkingsstatus bronStatus;
    private final Verwerkingsstatus doelStatus;
    private final LocalDateTime tijdstip;
    private final Map<String, String> metaInfo;

    /**
     * Constructor
     * @param bronStatus de verwerkingsstatus vanwaar vertrokken is.
     * @param doelStatus de nieuwe verwerkingsstatus
     * @param tijdstip het ogenblik waarop de transitie is geregistreerd
     * @param metaInfo bijkomende gegevens over de transitie die worden bijgehouden
     */
    public WorkItemTransition(Verwerkingsstatus bronStatus, Verwerkingsstatus doelStatus,
                              LocalDateTime tijdstip, Map<String, String> metaInfo) {
        this.bronStatus = bronStatus;
        this.doelStatus = doelStatus;
        this.tijdstip = tijdstip;
        this.metaInfo = metaInfo == null ? new HashMap<>() : metaInfo;
    }

    public Verwerkingsstatus getBronStatus() {
        return bronStatus;
    }

    public Verwerkingsstatus getDoelStatus() {
        return doelStatus;
    }

    public LocalDateTime getTijdstip() {
        return tijdstip;
    }

    public Map<String, String> getMetaInfo() {
        return metaInfo;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private Verwerkingsstatus from;
        private Verwerkingsstatus to;
        private LocalDateTime timeStamp;
        private Map<String, String> metaInfo = new HashMap<>();

        public Builder withFrom(Verwerkingsstatus from) {
            this.from = from;
            return this;
        }

        public Builder withTo(Verwerkingsstatus to) {
            this.to = to;
            return this;
        }

        public Builder withTimeStamp(LocalDateTime timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder withMetaInfo(Map<String, String> metaInfo) {
            this.metaInfo = new HashMap<>();
            this.metaInfo.putAll(metaInfo);
            return this;
        }

        public Builder withAdditionalMetaInfo(String key, String value) {
            this.metaInfo.put(key, value);
            return this;
        }

        public Builder but() {
            return new Builder().withFrom(from).withTo(to).withTimeStamp(timeStamp).withMetaInfo(metaInfo);
        }

        public WorkItemTransition build() {
            return new WorkItemTransition(from, to, timeStamp, metaInfo);
        }
    }
}

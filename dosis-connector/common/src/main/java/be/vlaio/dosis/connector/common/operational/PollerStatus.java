package be.vlaio.dosis.connector.common.operational;

import java.time.LocalDateTime;

public class PollerStatus {

    private final long currentItem;
    private final int nbItemsRetrieved;
    private final boolean active;
    private final String name;
    private final LocalDateTime lastPoll;
    private final LocalDateTime lastElementRetrievedAt;
    private final String lastResponse;
    private final int nbOfConsecutiveErrors;

    public PollerStatus(long currentItem, int nbItemsRetrieved, boolean active,
                          LocalDateTime lastPoll, LocalDateTime lastElementRetrievedAt,
                        String name, String lastResponse, int nbOfConsecutiveErrors) {
        this.currentItem = currentItem;
        this.nbItemsRetrieved = nbItemsRetrieved;
        this.active = active;
        this.lastPoll = lastPoll;
        this.lastElementRetrievedAt = lastElementRetrievedAt;
        this.name = name;
        this.lastResponse = lastResponse;
        this.nbOfConsecutiveErrors = nbOfConsecutiveErrors;
    }

    public int getNbOfConsecutiveErrors() {
        return nbOfConsecutiveErrors;
    }

    public long getCurrentItem() {
        return currentItem;
    }

    public int getNbItemsRetrieved() {
        return nbItemsRetrieved;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getLastPoll() {
        return lastPoll;
    }

    public LocalDateTime getLastElementRetrievedAt() {
        return lastElementRetrievedAt;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    public static final class Builder {
        private long currentItem;
        private int nbItemsRetrieved;
        private boolean active;
        private String name;
        private LocalDateTime lastPoll;
        private LocalDateTime lastElementRetrievedAt;
        private String lastResponse;
        private int nbConsecutiveErrors;

        public Builder withCurrentItem(long currentItem) {
            this.currentItem = currentItem;
            return this;
        }

        public Builder withNbItemsRetrieved(int nbItemsRetrieved) {
            this.nbItemsRetrieved = nbItemsRetrieved;
            return this;
        }

        public Builder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withConsecutiveErrors(int nbConsecutiveErrors) {
            this.nbConsecutiveErrors = nbConsecutiveErrors;
            return this;
        }

        public Builder withLastPoll(LocalDateTime lastPoll) {
            this.lastPoll = lastPoll;
            return this;
        }

        public Builder withLastElementRetrievedAt(LocalDateTime lastElementRetrievedAt) {
            this.lastElementRetrievedAt = lastElementRetrievedAt;
            return this;
        }

        public Builder withLastResponse(String response) {
            this.lastResponse = response;
            return this;
        }

        public PollerStatus build() {
            return new PollerStatus(currentItem, nbItemsRetrieved, active, lastPoll, lastElementRetrievedAt,
                    name, lastResponse, nbConsecutiveErrors);
        }
    }
}

package be.vlaio.dosis.connector.common;

import java.time.LocalDateTime;

public class PollerStatus {

    private int currentItem;
    private int nbItemsRetrieved;
    private boolean active;
    private LocalDateTime lastPoll;
    private LocalDateTime lastElementRetrievedAt;

    public PollerStatus(int currentItem, int nbItemsRetrieved, boolean active,
                          LocalDateTime lastPoll, LocalDateTime lastElementRetrievedAt) {
        this.currentItem = currentItem;
        this.nbItemsRetrieved = nbItemsRetrieved;
        this.active = active;
        this.lastPoll = lastPoll;
        this.lastElementRetrievedAt = lastElementRetrievedAt;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public int getNbItemsRetrieved() {
        return nbItemsRetrieved;
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


    public static final class Builder {
        private int currentItem;
        private int nbItemsRetrieved;
        private boolean active;
        private LocalDateTime lastPoll;
        private LocalDateTime lastElementRetrievedAt;

        public Builder withCurrentItem(int currentItem) {
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

        public Builder withLastPoll(LocalDateTime lastPoll) {
            this.lastPoll = lastPoll;
            return this;
        }

        public Builder withLastElementRetrievedAt(LocalDateTime lastElementRetrievedAt) {
            this.lastElementRetrievedAt = lastElementRetrievedAt;
            return this;
        }

        public PollerStatus build() {
            return new PollerStatus(currentItem, nbItemsRetrieved, active, lastPoll, lastElementRetrievedAt);
        }
    }
}

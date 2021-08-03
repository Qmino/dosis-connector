package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.PollerStatus;

import java.time.LocalDateTime;

public class PollerStatusTO {

    private long currentItem;
    private int nbItemsRetrieved;
    private boolean active;
    private LocalDateTime lastPoll;
    private LocalDateTime lastElementRetrievedAt;

    public PollerStatusTO(long currentItem, int nbItemsRetrieved, boolean active,
                          LocalDateTime lastPoll, LocalDateTime lastElementRetrievedAt) {
        this.currentItem = currentItem;
        this.nbItemsRetrieved = nbItemsRetrieved;
        this.active = active;
        this.lastPoll = lastPoll;
        this.lastElementRetrievedAt = lastElementRetrievedAt;
    }

    public long getCurrentItem() {
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
        private long currentItem;
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

        public Builder from(PollerStatus status) {
            this.active = status.isActive();
            this.currentItem = status.getCurrentItem();
            this.lastPoll = status.getLastPoll();
            this.lastElementRetrievedAt = status.getLastElementRetrievedAt();
            this.nbItemsRetrieved = status.getNbItemsRetrieved();
            return this;
        }

        public PollerStatusTO build() {
            return new PollerStatusTO(currentItem, nbItemsRetrieved, active, lastPoll, lastElementRetrievedAt);
        }
    }
}

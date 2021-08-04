package be.vlaio.dosis.connector.common;

import java.time.LocalDateTime;

public class PollerStatus {

    private final long currentItem;
    private final int nbItemsRetrieved;
    private final boolean active;
    private String name;
    private final LocalDateTime lastPoll;
    private final LocalDateTime lastElementRetrievedAt;

    public PollerStatus(long currentItem, int nbItemsRetrieved, boolean active,
                          LocalDateTime lastPoll, LocalDateTime lastElementRetrievedAt,
                        String name) {
        this.currentItem = currentItem;
        this.nbItemsRetrieved = nbItemsRetrieved;
        this.active = active;
        this.lastPoll = lastPoll;
        this.lastElementRetrievedAt = lastElementRetrievedAt;
        this.name = name;
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


    public static final class Builder {
        private long currentItem;
        private int nbItemsRetrieved;
        private boolean active;
        private String name;
        private LocalDateTime lastPoll;
        private LocalDateTime lastElementRetrievedAt;

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


        public Builder withLastPoll(LocalDateTime lastPoll) {
            this.lastPoll = lastPoll;
            return this;
        }

        public Builder withLastElementRetrievedAt(LocalDateTime lastElementRetrievedAt) {
            this.lastElementRetrievedAt = lastElementRetrievedAt;
            return this;
        }

        public PollerStatus build() {
            return new PollerStatus(currentItem, nbItemsRetrieved, active, lastPoll, lastElementRetrievedAt, name);
        }
    }
}

package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.operational.PollerStatus;

import java.time.LocalDateTime;

public class PollerStatusTO {

    private final long currentItem;
    private final int nbItemsRetrieved;
    private final boolean active;
    private final LocalDateTime lastPoll;
    private final LocalDateTime lastElementRetrievedAt;
    private final String lastResult;
    private final String name;
    private final int numberOfConsecutiveErrors;

    public PollerStatusTO(long currentItem, int nbItemsRetrieved, boolean active,
                          LocalDateTime lastPoll, LocalDateTime lastElementRetrievedAt,
                          String lastResult, String name, int numberOfConsecutiveErrors) {
        this.currentItem = currentItem;
        this.nbItemsRetrieved = nbItemsRetrieved;
        this.active = active;
        this.lastPoll = lastPoll;
        this.lastElementRetrievedAt = lastElementRetrievedAt;
        this.lastResult = lastResult;
        this.name = name;
        this.numberOfConsecutiveErrors = numberOfConsecutiveErrors;
    }

    public String getName() {
        return name;
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

    public String getLastResult() {
        return lastResult;
    }

    public int getNumberOfConsecutiveErrors() {
        return numberOfConsecutiveErrors;
    }

    public static final class Builder {
        private long currentItem;
        private int nbItemsRetrieved;
        private boolean active;
        private LocalDateTime lastPoll;
        private LocalDateTime lastElementRetrievedAt;
        private String lastResult;
        private String name;
        private int numberOfConsecutiveErrors;

        public Builder withCurrentItem(int currentItem) {
            this.currentItem = currentItem;
            return this;
        }

        public Builder withLastResult(String string) {
            this.lastResult = lastResult;
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

        public Builder withNumberOfConsecutiveErrors(int numberOfConsecutiveErrors) {
            this.numberOfConsecutiveErrors = numberOfConsecutiveErrors;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder from(PollerStatus status) {
            this.active = status.isActive();
            this.currentItem = status.getCurrentItem();
            this.lastPoll = status.getLastPoll();
            this.lastElementRetrievedAt = status.getLastElementRetrievedAt();
            this.nbItemsRetrieved = status.getNbItemsRetrieved();
            this.lastResult = status.getLastResponse();
            this.name = status.getName();
            this.numberOfConsecutiveErrors = status.getNbOfConsecutiveErrors();
            return this;
        }

        public PollerStatusTO build() {
            return new PollerStatusTO(currentItem, nbItemsRetrieved, active, lastPoll, lastElementRetrievedAt,
                    lastResult, name, numberOfConsecutiveErrors);
        }
    }
}

package be.vlaio.dosis.connector.managementapi.dto;

import be.vlaio.dosis.connector.common.Verwerkingsstatus;
import be.vlaio.dosis.connector.common.WipStatus;

import java.util.HashMap;
import java.util.Map;

public class WipStatusTO {

    private Map<Verwerkingsstatus, Integer> items;
    private int highWaterMark;
    private int lowWaterMark;
    private boolean acceptingWork;

    public WipStatusTO(Map<Verwerkingsstatus, Integer> items, int highWaterMark,
                       int lowWaterMark, boolean acceptingWork) {
        this.items = items;
        this.highWaterMark = highWaterMark;
        this.lowWaterMark = lowWaterMark;
        this.acceptingWork = acceptingWork;
    }

    public Map<Verwerkingsstatus, Integer> getItems() {
        return items;
    }

    public int getHighWaterMark() {
        return highWaterMark;
    }

    public int getLowWaterMark() {
        return lowWaterMark;
    }

    public boolean isAcceptingWork() {
        return acceptingWork;
    }


    public static final class Builder {
        private Map<Verwerkingsstatus, Integer> items;
        private int highWaterMark;
        private int lowWaterMark;
        private boolean acceptingWork;

        public Builder() {
        }

        public Builder withItems(Map<Verwerkingsstatus, Integer> items) {
            this.items = items;
            return this;
        }

        public Builder withHighWaterMark(int highWaterMark) {
            this.highWaterMark = highWaterMark;
            return this;
        }

        public Builder withLowWaterMark(int lowWaterMark) {
            this.lowWaterMark = lowWaterMark;
            return this;
        }

        public Builder withAcceptingWork(boolean acceptingWork) {
            this.acceptingWork = acceptingWork;
            return this;
        }

        public Builder from(WipStatus status) {
            this.acceptingWork = status.isAcceptingWork();
            this.highWaterMark = status.getHighWaterMark();
            this.lowWaterMark = status.getLowWaterMark();
            this.items = new HashMap<>(status.getItems());
            return this;
        }

        public WipStatusTO build() {
            return new WipStatusTO(items, highWaterMark, lowWaterMark, acceptingWork);
        }
    }
}

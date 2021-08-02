package be.vlaio.dosis.connector.common;

import java.util.HashMap;
import java.util.Map;

public class WipStatus {

    private Map<Verwerkingsstatus, Integer> items;
    private int highWaterMark;
    private int lowWaterMark;
    private boolean acceptingWork;

    public WipStatus(Map<Verwerkingsstatus, Integer> items, int highWaterMark,
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
            this.items = new HashMap<>(items);
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

        public WipStatus build() {
            return new WipStatus(items, highWaterMark, lowWaterMark, acceptingWork);
        }
    }
}

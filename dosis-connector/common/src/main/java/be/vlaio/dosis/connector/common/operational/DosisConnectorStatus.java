package be.vlaio.dosis.connector.common.operational;

import java.util.ArrayList;
import java.util.List;

public class DosisConnectorStatus {

    private List<PollerStatus> pollers;
    private WipStatus workInProgress;

    public List<PollerStatus> getPollers() {
        return pollers;
    }

    public WipStatus getWorkInProgress() {
        return workInProgress;
    }

    public DosisConnectorStatus(List<PollerStatus> pollers, WipStatus workInProgress) {
        this.pollers = pollers;
        this.workInProgress = workInProgress;
    }

    public static final class Builder {
        private List<PollerStatus> pollers;
        private WipStatus workInProgress;

        public Builder() {
        }

        public Builder withPollers(List<PollerStatus> pollers) {
            this.pollers = new ArrayList<>();
            this.pollers.addAll(pollers);
            return this;
        }

        public Builder withWorkInProgress(WipStatus workInProgress) {
            this.workInProgress = workInProgress;
            return this;
        }

        public DosisConnectorStatus build() {
            return new DosisConnectorStatus(pollers, workInProgress);
        }
    }
}

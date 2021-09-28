package be.vlaio.dosis.connector.common.operational;

import java.time.LocalDateTime;

public class PusherValidatorStatus {

    private final boolean active;
    private final LocalDateTime lastCallAttempt;
    private final LocalDateTime lastCallExecuted;
    private final String lastResult;
    private final int numberOfConsecutiveErrors;

    public PusherValidatorStatus(boolean active,
                             LocalDateTime lastCallAttempt,
                             LocalDateTime lastCallExecuted,
                             String lastResult,
                             int numberOfConsecutiveErrors) {
        this.active = active;
        this.lastCallAttempt = lastCallAttempt;
        this.lastCallExecuted = lastCallExecuted;
        this.lastResult = lastResult;
        this.numberOfConsecutiveErrors = numberOfConsecutiveErrors;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getLastCallAttempt() {
        return lastCallAttempt;
    }

    public LocalDateTime getLastCallExecuted() {
        return lastCallExecuted;
    }

    public String getLastResult() {
        return lastResult;
    }

    public int getNumberOfConsecutiveErrors() {
        return numberOfConsecutiveErrors;
    }


    public static final class Builder {
        private boolean active;
        private LocalDateTime lastCallAttempt;
        private LocalDateTime lastCallExecuted;
        private String lastResult;
        private int numberOfConsecutiveErrors;

        public Builder() {
        }

        public Builder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder withLastCallAttempt(LocalDateTime lastCallAttempt) {
            this.lastCallAttempt = lastCallAttempt;
            return this;
        }

        public Builder withLastCallExecuted(LocalDateTime lastCallExecuted) {
            this.lastCallExecuted = lastCallExecuted;
            return this;
        }

        public Builder withLastResult(String lastResult) {
            this.lastResult = lastResult;
            return this;
        }

        public Builder withNumberOfConsecutiveErrors(int numberOfConsecutiveErrors) {
            this.numberOfConsecutiveErrors = numberOfConsecutiveErrors;
            return this;
        }

        public Builder but() {
            return new Builder()
                    .withActive(active)
                    .withLastCallAttempt(lastCallAttempt)
                    .withLastCallExecuted(lastCallExecuted)
                    .withLastResult(lastResult)
                    .withNumberOfConsecutiveErrors(numberOfConsecutiveErrors);
        }

        public PusherValidatorStatus build() {
            return new PusherValidatorStatus(active, lastCallAttempt, lastCallExecuted, lastResult, numberOfConsecutiveErrors);
        }
    }
}

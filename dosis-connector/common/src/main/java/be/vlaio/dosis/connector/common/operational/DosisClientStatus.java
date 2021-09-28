package be.vlaio.dosis.connector.common.operational;

import java.time.LocalDateTime;

public class DosisClientStatus {

    private final String clientId;
    private final String authorizationUrl;
    private final String baseServiceUrl;
    private final String accessToken;
    private final boolean authenticated;
    private final LocalDateTime accessTokenExpiration;

    public DosisClientStatus(String clientId,
                             String authorizationUrl,
                             String baseServiceUrl,
                             String accessToken,
                             LocalDateTime accessTokenExpiration,
                             boolean authenticated) {
        this.clientId = clientId;
        this.authorizationUrl = authorizationUrl;
        this.baseServiceUrl = baseServiceUrl;
        this.accessToken = accessToken;
        this.accessTokenExpiration = accessTokenExpiration;
        this.authenticated = authenticated;
    }

    public String getClientId() {
        return clientId;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public String getBaseServiceUrl() {
        return baseServiceUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public LocalDateTime getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public static final class Builder {
        private String clientId;
        private String authorizationUrl;
        private String baseServiceUrl;
        private String accessToken;
        private boolean authenticated;
        private LocalDateTime accessTokenExpiration;

        public Builder() {
        }

        public Builder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder withAuthorizationUrl(String authorizationUrl) {
            this.authorizationUrl = authorizationUrl;
            return this;
        }

        public Builder withBaseServiceUrl(String baseServiceUrl) {
            this.baseServiceUrl = baseServiceUrl;
            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withAccessTokenExpiration(LocalDateTime accessTokenExpiration) {
            this.accessTokenExpiration = accessTokenExpiration;
            return this;
        }

        public Builder withAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public DosisClientStatus build() {
            return new DosisClientStatus(
                    clientId,
                    authorizationUrl,
                    baseServiceUrl,
                    accessToken,
                    accessTokenExpiration,
                    authenticated);
        }
    }
}

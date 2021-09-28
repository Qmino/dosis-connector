package be.vlaio.dosis.connector.springconf;

import be.vlaio.dosis.connector.common.operational.PollerSpecification;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="dosisgateway.dosis")
public class DosisConfiguration {

    private String clientId;
    private String tokenUrl;
    private String baseUrl;
    private String bronUri;
    private String privateWebKey;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBronUri() {
        return bronUri;
    }

    public void setBronUri(String bronUri) {
        this.bronUri = bronUri;
    }

    public String getPrivateWebKey() {
        return privateWebKey;
    }

    public void setPrivateWebKey(String privateWebKey) {
        this.privateWebKey = privateWebKey;
    }
}

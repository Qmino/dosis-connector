package be.vlaio.dosis.connector.pusher.dosis;

import com.nimbusds.jose.JWSAlgorithm;

import java.security.interfaces.RSAPrivateKey;

/**
 * Holding object voor key informatie
 */
public class DosisKeyInfo {

    private RSAPrivateKey privateKey;
    private String keyId;
    private JWSAlgorithm algorithm;

    public DosisKeyInfo(RSAPrivateKey privateKey, String keyId, JWSAlgorithm algorithm) {
        this.privateKey = privateKey;
        this.keyId = keyId;
        this.algorithm = algorithm;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}

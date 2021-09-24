package be.vlaio.dosis.connector.pusher.dosis;

import be.vlaio.dosis.connector.common.ServiceError;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierTO;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierUploadStatusTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetails;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionFactory;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * <p>
 * De dosisclient is de klasse die effectief verantwoordelijk is voor de interactie met de DOSIS services.
 * Verantwoordelijkheden omvatten het ophalen van en refreshen van het authorizatie token, en het uitvoeren van de
 * rest calls, alsook het wrappen van de responses en afhandelen van fouten via een exception mechanisme.
 * </p>
 *
 * <p>
 * Voor authenticatie en authorizatie wordt gebruik gemaakt geosecure. De API zelf is beveiligd via OAuth 2.0
 * (Client Credentials Grant Flow). Als input heeft de DosisClient toegang nodig tot relevante velden die je kan halen
 * uit het JSON Web Token. Uitleg over hoe dit token bekomen kan worden, en hoe het publieke deel moet geconfigureerd
 * worden binnen dosis kan gevonden worden op
 * <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/IKPubliek/pages/2680390681/Beveiliging+van+de+DOSIS-API">
 * deze</a> url.
 * </p>
 * <p>
 * Gebruik van de dosisclient is beperkt tot de pusher.
 *
 * @author <a href="http://www.qmino.com">Yves Vandewoude</a>
 */
public class DosisClient {

    private int timeOut = 5000;
    private RestTemplate restTemplate;

    private DosisKeyInfo key;
    private String clientId;
    private String authorizationUrl;
    private String baseServiceUrl;
    private String accessToken;
    private LocalDateTime accessTokenExpiration;

    private static final Logger LOGGER = LoggerFactory.getLogger(DosisClient.class);

    /**
     * Constructor van een dosisclient.
     *
     * @param clientId       de id van de oauth client. Deze wordt aangeleverd door DOSIS.
     * @param baseServiceUrl de base url voor alle rest calls naar dosis
     * @param tokenUrl  de url waar de accesstoken zal worden opgevraagd
     * @param privateKeyFile de private key van de json web token die gegenereerd is.
     */
    public DosisClient(String clientId, String baseServiceUrl, String tokenUrl, String privateKeyFile) throws DosisClientException {
        this.clientId = clientId;
        setUrls(baseServiceUrl, tokenUrl);
        key = readPrivateKey(privateKeyFile);
        restTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    /**
     * Laadt een dossierstatus op bij Dosis.
     * @param dossierStatusTO de op te laden dossierstatus
     */
    public DosisDossierUploadStatusTO laadDossierStatusOp(DosisDossierTO dossierStatusTO) throws DosisClientException {
        if (shouldRefreshAccessToken()) {
            refreshAccessToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", accessToken);
        HttpEntity<DosisDossierTO> entity = new HttpEntity<>(dossierStatusTO, headers);
        ResponseEntity<DosisDossierUploadStatusTO> exchange = null;
        String url = baseServiceUrl + DosisConstanten.DOSSIER_STATUS_UPLOAD_URL;
        try {
            LOGGER.debug("Sending item [DossierNummer: " + dossierStatusTO.getIdentificatie().getDossierNummer()
                    + ", UploadId: " + dossierStatusTO.getUploadId() + "] to DOSIS: " + url);
            exchange
                    = restTemplate.exchange(url,
                    HttpMethod.POST,
                    entity,
                    DosisDossierUploadStatusTO.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                DosisDossierUploadStatusTO result = exchange.getBody();
                if (result == null) {
                    throw new DosisClientInteractionException("Onverwacht resultaat", null, ServiceError.UNEXPECTED_CONTENT_ERROR, exchange);
                } else {
                    return result;
                }
            } else {
                // No idea how this can happen.
                throw new DosisClientException("Unknown Error", ServiceError.UNKNOWN);
            }
        } catch (RestClientException rce) {
            if (rce instanceof RestClientResponseException) {
                RestClientResponseException rcre = (RestClientResponseException) rce;
                if (HttpStatus.valueOf(rcre.getRawStatusCode()).is4xxClientError()) {
                    throw new DosisClientInteractionException(rce.getMessage(), rce, ServiceError.CLIENT_ERROR, exchange);
                } else if (HttpStatus.valueOf(rcre.getRawStatusCode()).is5xxServerError()) {
                    throw new DosisClientInteractionException(rce.getMessage(), rce, ServiceError.SERVER_ERROR, exchange);
                } else {
                    // No idea how this can happen.
                    throw new DosisClientInteractionException("Ongekende fout", rce, ServiceError.UNKNOWN, exchange);
                }
            } else if (rce.getMessage() != null && rce.getMessage().toLowerCase().contains("timeout")) {
                throw new DosisClientInteractionException(rce.getMessage(), rce, ServiceError.TIMEOUT, exchange);
            } else {
                throw new DosisClientInteractionException(rce.getMessage(), rce, ServiceError.UNKNOWN, exchange);
            }
        }
    }


    /**
     * Haalt een accesstoken op en bewaart zowel dit token als de expiration ervan in deze client.
     * @throws DosisClientException indien het token niet kan worden opgehaald.
     */
    public void refreshAccessToken() throws DosisClientException {
        try {
            // Construct the client credentials grant
            AuthorizationGrant clientGrant = new ClientCredentialsGrant();
            long currentTimeStampInSeconds = System.currentTimeMillis() / 1000;

            // The credentials to authenticate the client at the token endpoint
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("iss", clientId);
            detailsMap.put("sub", clientId);
            detailsMap.put("jti", UUID.randomUUID().toString());
            detailsMap.put("exp", (currentTimeStampInSeconds + 120));
            detailsMap.put("iat", currentTimeStampInSeconds);
            detailsMap.put("aud", authorizationUrl);

            PrivateKeyJWT privateKeyJWT = new PrivateKeyJWT(
                    JWTAssertionFactory.create(
                            JWTAssertionDetails.parse(new JSONObject(detailsMap)),
                            key.getAlgorithm(),
                            key.getPrivateKey(),
                            key.getKeyId(),
                            null
                    )
            );

            // Make the token request
            TokenRequest request = new TokenRequest(
                    new URI(authorizationUrl),
                    privateKeyJWT,
                    clientGrant,
                    new Scope(DosisConstanten.SCOPE));

            TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());
            if (!response.indicatesSuccess()) {
                // We got an error response...
                TokenErrorResponse errorResponse = response.toErrorResponse();
                throw new DosisClientException(errorResponse.getErrorObject().toString());
            }
            AccessTokenResponse successResponse = response.toSuccessResponse();
            // Get the access token
            AccessToken accessToken = successResponse.getTokens().getAccessToken();
            accessTokenExpiration = LocalDateTime.now().plusSeconds(accessToken.getLifetime());
            this.accessToken = "Bearer: " + Base64.getEncoder().encodeToString(accessToken.toString().getBytes());
        } catch (com.nimbusds.oauth2.sdk.ParseException | JOSEException e) {
            throw new DosisClientException("Probleem bij het opstellen van assertion bij het ophalen van het accesstoken", e);
        } catch (URISyntaxException | IOException e) {
            throw new DosisClientException("Probleem bij het contacteren van de server voor het ophalen van het accesstoken", e);
        }
    }

    /**
     * @return true indien het accesstoken moet vernieuwd worden, false indien niet.
     */
    public boolean shouldRefreshAccessToken() {
        return accessTokenExpiration == null ||
                accessTokenExpiration.isBefore(LocalDateTime.now().plusSeconds(DosisConstanten.REFRESH_MARGIN));
    }

    /**
     * @return een clientrequestfactory aan op basis van de gespecificeerde timeout.
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeOut);
        clientHttpRequestFactory.setReadTimeout(timeOut);
        clientHttpRequestFactory.setConnectionRequestTimeout(timeOut);
        return clientHttpRequestFactory;
    }

    /**
     * Configureert de te gebruiken urls op basis van de te gebruiken dosis omgeving.
     *
     * @param baseUrl de baseUrl die gebruikt wordt voor de API calls
     * @param tokenUrl de url die gebruikt wordt voor de accesstoken op te vragen
     */
    private void setUrls(String baseUrl, String tokenUrl) {
        authorizationUrl = tokenUrl;
        baseServiceUrl = baseUrl;
        LOGGER.debug("DOSIS API urls set to: [" + tokenUrl + " , " + baseUrl + "]");
    }

    /**
     * Hulpmethode die de inhoud van het private key bestand uitleest, en dit omzet naar een java key
     *
     * @param fileName de bestandsnaam van de private key
     * @return Het key object
     * @throws DosisClientException indien de key niet kan worden uitgelezen of omgezet
     */
    private DosisKeyInfo readPrivateKey(String fileName) throws DosisClientException {
        try {
            String jsonKeyFileContent = new String(Files.readAllBytes(Paths.get(fileName)));
            Map<String, String> keyFields = new ObjectMapper().readValue(jsonKeyFileContent, new TypeReference<>() {});
            JWSAlgorithm algo = JWSAlgorithm.parse(keyFields.get("alg"));
            String type = keyFields.get("kty");
            if (! "RSA".equalsIgnoreCase(type)) {
                throw new DosisClientException("Enkel RSA keys worden momenteel door de dosis connector ondersteund.");
            }
            String keyId = keyFields.get("kid");
            JWK jwk = JWK.parse(jsonKeyFileContent);
            return new DosisKeyInfo(jwk.toRSAKey().toRSAPrivateKey(), keyId, algo);
        } catch (IOException e) {
            throw new DosisClientException("Probleem met het lezen van het private key bestand: " + fileName, e);
        } catch (ParseException | JOSEException e) {
            throw new DosisClientException("Ongelidge structuur van de private key in " + fileName, e);
        }
    }
}

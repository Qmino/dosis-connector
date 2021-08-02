package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * Klasse die verantwoordelijk is om, op aanvraag van een poller, een call uit te voeren naar een specifiek dossier
 * beheersysteem. De klasse doet enkel de restcall, niets anders.
 */
public class DossierbeheersysteemFetcher {

    private int timeOut = 5000;
    private RestTemplate restTemplate;
    private final String fetchUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(DossierbeheersysteemFetcher.class);

    /**
     * Constructor die een dossierbeheerfetcher maakt op basis van de basisURL. De onderliggende endpoints
     * worden bepaald door de meegeleverde specificatie.
     *
     * @param baseUrl de url waar het dossierbeheersysteem kan worden geraadpleegd.
     */
    public DossierbeheersysteemFetcher(String baseUrl) {
        fetchUrl = baseUrl.endsWith("/")
                ? baseUrl + "dossierstatusveranderingen?index={index}&limiet={limiet}"
                : baseUrl + "/dossierstatusveranderingen?index={index}&limiet={limiet}";
        restTemplate = new RestTemplate(getClientHttpRequestFactory());
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
     * @return de huidig geconfigureerde timeout.
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * Wijzit de timeout van de dossierbeheerfetcher
     *
     * @param timeOut de nieuwe timeout, uitgedrukt in ms (strict positief)
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        restTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    /**
     * Voert een rest call uit naar het dossierbeheersysteem.
     *
     * @param index  de index van het eerst op te halen element
     * @param limiet de maximaal aantal elementen dat de client zal opvragen
     * @return een dossierstatusverzameling, met daarin maximaal limiet aantal elementen
     * @throws FetchException indien er een probleem was en de resultaten niet konden worden opgehaald.
     */
    public DossierStatusCollectionTO fetchItems(int index, int limiet) throws FetchException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<DossierStatusCollectionTO> exchange = null;
        try {
            exchange
                    = restTemplate.exchange(fetchUrl,
                    HttpMethod.GET,
                    entity,
                    DossierStatusCollectionTO.class,
                    index,
                    limiet);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                DossierStatusCollectionTO result = exchange.getBody();
                if (result == null || (!"StatusVeranderingsVerzameling".equals(result.getType()) && result.getElementen() == null)) {
                    throw new FetchException("Onverwacht resultaat", null, ServiceError.UNEXPECTED_CONTENT_ERROR, exchange);
                } else {
                    return result;
                }
            } else {
                // No idea how this can happen.
                throw new FetchException("", null, ServiceError.UNKNOWN, exchange);
            }
        } catch (RestClientException rce) {
            if (rce instanceof RestClientResponseException) {
                RestClientResponseException rcre = (RestClientResponseException) rce;
                if (HttpStatus.valueOf(rcre.getRawStatusCode()).is4xxClientError()) {
                    throw new FetchException("", null, ServiceError.CLIENT_ERROR, exchange);
                } else if (HttpStatus.valueOf(rcre.getRawStatusCode()).is5xxServerError()) {
                    throw new FetchException("", null, ServiceError.SERVER_ERROR, exchange);
                } else {
                    // No idea how this can happen.
                    throw new FetchException("", null, ServiceError.UNKNOWN, exchange);
                }
            } else if (rce.getMessage() != null && rce.getMessage().toLowerCase().contains("timeout")) {
                throw new FetchException(rce.getMessage(), rce, ServiceError.TIMEOUT, exchange);
            } else {
                throw new FetchException(rce.getMessage(), rce, ServiceError.UNKNOWN, exchange);
            }
        }
    }
}

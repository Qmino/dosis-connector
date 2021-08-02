package be.vlaio.dosis.connector.poller.dossierbeheersysteem;

import be.vlaio.dosis.connector.poller.Poller;
import be.vlaio.dosis.connector.poller.dossierbeheersysteem.dto.DossierStatusCollectionTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



/**
 * Klasse die verantwoordelijk is om, op aanvraag van een poller, een call uit te voeren naar een specifiek dossier
 * beheersysteem. De klasse doet enkel de restcall, niets anders.
 */
public class DossierbeheersysteemFetcher {

    private int timeOut = 5000;
    private RestTemplate restTemplate;
    private String fetchUrl;
    private static final Logger LOGGER = LoggerFactory.getLogger(DossierbeheersysteemFetcher.class);

    public DossierbeheersysteemFetcher(String baseUrl) {
        fetchUrl = baseUrl.endsWith("/")
                ? baseUrl + "dossierstatusveranderingen?index={index}&limiet={limiet}"
                : baseUrl + "/dossierstatusveranderingen?index={index}&limiet={limiet}";
        restTemplate = new RestTemplate(getClientHttpRequestFactory());

    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeOut);
        return clientHttpRequestFactory;
    }

    public DossierStatusCollectionTO fetchItems(int index, int limiet) throws FetchException {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        try {
            final ResponseEntity<DossierStatusCollectionTO> exchange
                    = restTemplate.exchange(fetchUrl,
                    HttpMethod.GET,
                    entity,
                    DossierStatusCollectionTO.class,
                    index,
                    limiet);
            if (exchange.getStatusCode() == HttpStatus.ACCEPTED) {
                return exchange.getBody();
            } else {
                throw new FetchException(exchange);
            }
        } catch (RestClientException rce) {
            LOGGER.warn("Problem retrieving content", rce);
            return null;
        }
    }
}

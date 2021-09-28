package be.vlaio.dosis.connector.pusher.dosis;

import be.vlaio.dosis.connector.common.dosisdomain.DosisItem;
import be.vlaio.dosis.connector.pusher.dosis.dto.DosisDossierTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Maakt een dosisdossier voor verzending naar de dosis services op basis van een DosisItem
 */
@Component
public class DosisTOFactory {

    private String bronUri;
    private static final Logger LOGGER = LoggerFactory.getLogger(DosisTOFactory.class);

    public DosisTOFactory(@Value("${dosisgateway.dosis.bronuri}") String bronUri) {
        this.bronUri = bronUri;
    }

    /**
     *
     * @param item het item dat moet worden omgezet naar een dosis dto.
     * @return een dto voorstelling van het DosisItem
     */
    public DosisDossierTO from(DosisItem item) {
        return new DosisDossierTO.Builder().from(item, bronUri).build();
    }
}

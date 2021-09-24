package be.vlaio.dosis.connector.common;

/**
 * Beschrijft de verschillende categorieën aan fouten die de externe servers kunnen teruggeven.
 */
public enum ServiceError {

    /**
     * Het dossierbeheersysteem reageert niet binnen de timeout periode van de poller.
     */
    TIMEOUT,
    /**
     * Het dossiersysteem reageert met een 4xx: dit betekent in feite een foute parameter/bug in dosisconnector
     */
    CLIENT_ERROR,
    /**
     * Onverwachte serverfout (5xx)
     */
    SERVER_ERROR,
    /**
     * Server geeft response, maar deze kan niet gedeserialiseerd worden door dosisconnector omdat deze een onverwachte
     * structuur heeft.
     */
    UNEXPECTED_CONTENT_ERROR,
    /**
     * het type fout is ongekend.
     */
    UNKNOWN
}

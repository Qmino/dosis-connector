package be.vlaio.dosis.connector.pusher.dosis;

/**
 * Constanten die hardcoded zijn voor de dosisservices. Ze worden daarom niet via properties configureerbaar aangeboden.
 */
public class DosisConstanten {



    public static final String DOSSIER_STATUS_UPLOAD_URL = "/api/v1/dosis/dossiers";

    public static final String GRANT_TYPE = "client_credentials";
    public static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    public static final String SCOPE = "DosisImport"; // "DOSISImport";
    public static final int REFRESH_MARGIN = 300;
    public static final String BURGERRECHT = "Raadpleger";
}

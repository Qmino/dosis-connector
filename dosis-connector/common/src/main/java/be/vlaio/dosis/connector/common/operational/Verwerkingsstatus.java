package be.vlaio.dosis.connector.common.operational;

/**
 * De verwerkingsstatus van dit item in de dosisconnector. Deze status is ongerelateerd aan de dosis status.
 */
public enum Verwerkingsstatus {
    /**
     * Item is gekend bij de dosis connector, maar nog niet naar dosis verstuurd.
     */
    TODO,
    /**
     * Het item is verstuurd naar dosis, en daar aanvaard, maar er is nog geen validatie informatie beschikbaar.
     */
    UNVALIDATED,
    /**
     * Het item is succesvol doorgestuurd naar dosis en daar aanvaard.
     */
    COMPLETED,
    /**
     * Het item werd doorgestuurd naar dosis, maar is daar geweigerd door validatie fouten.
     */
    FAILED
}

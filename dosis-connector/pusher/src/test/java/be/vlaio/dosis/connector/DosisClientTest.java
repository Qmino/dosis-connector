package be.vlaio.dosis.connector;

import be.vlaio.dosis.connector.pusher.dosis.DosisClient;
import be.vlaio.dosis.connector.pusher.dosis.DosisClientException;
import org.junit.jupiter.api.Test;

public class DosisClientTest {

    @Test
    public void otherTest() throws DosisClientException {
        DosisClient client = new DosisClient("6775",
                "https://beta.dosis.dev-vlaanderen.be",
                "https://beta.oauth.vlaanderen.be/authorization/ws/oauth/v2/token",
                "../jsonwebkeyprivate.key",
                true);
        client.refreshAccessToken();
        System.out.println(client);
    }
}

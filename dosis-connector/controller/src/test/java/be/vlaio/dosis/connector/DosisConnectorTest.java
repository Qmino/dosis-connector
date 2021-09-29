package be.vlaio.dosis.connector;

import static org.assertj.core.api.Assertions.assertThat;

import be.vlaio.dosis.connector.controller.DosisController;
import be.vlaio.dosis.connector.pusher.Pusher;
import be.vlaio.dosis.connector.pusher.Validator;
import be.vlaio.dosis.connector.pusher.dosis.DosisClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DosisConnectorTest {

	@Autowired
	private DosisController controllerCommandLineRunner;
	@Autowired
	private Pusher pusher;
	@Autowired
	private Validator validator;
	@Autowired
	private DosisClient client;

	@Test
	void contextLoads() {
		assertThat(controllerCommandLineRunner).isNotNull();
		assertThat(pusher).isNotNull();
		assertThat(validator).isNotNull();
		assertThat(client).isNotNull();
	}
}

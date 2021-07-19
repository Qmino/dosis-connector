package be.vlaio.dosis.connector.wip;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DosisConnectorTest {

	@Autowired
	private ControllerCommandLineRunner controllerCommandLineRunner;

	@Autowired
	private WipCommandLineRunner wipCommandLineRunner;

	@Autowired
	private PollerCommandLineRunner pollerCommandLineRunner;

	@Autowired
	private PusherCommandLineRunner pusherCommandLineRunner;

	@Test
	void contextLoads() {
		assertThat(controllerCommandLineRunner).isNotNull();
		assertThat(wipCommandLineRunner).isNotNull();
		assertThat(pollerCommandLineRunner).isNotNull();
		assertThat(pusherCommandLineRunner).isNotNull();
	}
}

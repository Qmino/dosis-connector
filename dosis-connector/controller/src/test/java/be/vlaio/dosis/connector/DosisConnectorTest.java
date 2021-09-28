package be.vlaio.dosis.connector;

import static org.assertj.core.api.Assertions.assertThat;

import be.vlaio.dosis.connector.controller.DosisController;
import be.vlaio.dosis.connector.wip.PollerCommandLineRunner;
import be.vlaio.dosis.connector.wip.WipCommandLineRunner;
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
	private WipCommandLineRunner wipCommandLineRunner;

	@Autowired
	private PollerCommandLineRunner pollerCommandLineRunner;

//	@Autowired
//	private PusherCommandLineRunner pusherCommandLineRunner;

	@Test
	void contextLoads() {
		assertThat(controllerCommandLineRunner).isNotNull();
		assertThat(wipCommandLineRunner).isNotNull();
		assertThat(pollerCommandLineRunner).isNotNull();
	//	assertThat(pusherCommandLineRunner).isNotNull();
	}
}

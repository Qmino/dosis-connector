package be.vlaio.dosis.connector.wip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WipCommandLineRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(WipCommandLineRunner.class);

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info("Hello from {}", this.getClass().getSimpleName());
	}
}

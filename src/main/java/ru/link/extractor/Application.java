package ru.link.extractor;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		for (String arg: args) {
			if (arg.contains("--help")) {
				System.out.println(getHelp());
				System.exit(0);
			}
		}
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.OFF);
		app.run(args);
	}

	private static String getHelp() {
		try (InputStream stream = new ClassPathResource("help.info").getInputStream()) {
			return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "Error with getting help";
		}
	}

}



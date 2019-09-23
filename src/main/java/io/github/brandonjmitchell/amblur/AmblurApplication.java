package io.github.brandonjmitchell.amblur;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.brandonjmitchell.amblur")
public class AmblurApplication implements CommandLineRunner {
	public static void main(String[] args) {
        SpringApplication.run(AmblurApplication.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
		// Nothing to run here.
	}
}

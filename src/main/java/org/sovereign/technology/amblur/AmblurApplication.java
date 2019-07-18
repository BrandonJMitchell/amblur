package org.sovereign.technology.amblur;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.sovereign.technology.amblur")
public class AmblurApplication implements CommandLineRunner {
	public static void main(String[] args) {
        SpringApplication.run(AmblurApplication.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
		
	}
}

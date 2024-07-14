package amch.labs.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxEssentialsApplication {
	static {
		BlockHound.install();
    }
	public static void main(String[] args) {
		System.out.println("JVM Arguments: " + System.getProperty("sun.java.command"));
		SpringApplication.run(SpringWebfluxEssentialsApplication.class, args);
	}

}

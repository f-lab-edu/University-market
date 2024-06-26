package university.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:.env")
public class MarketApplication {
	public static void main(String[] args) {
		SpringApplication.run(MarketApplication.class, args);
	}
}

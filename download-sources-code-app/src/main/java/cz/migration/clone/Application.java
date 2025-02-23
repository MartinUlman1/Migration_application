package cz.migration.clone;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Application {

	@Value("${token.github}")
	String GITHUB_TOKEN;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public WebClient github() {
		return WebClient
				.builder()
				.baseUrl("https://api.github.com/")
				.defaultHeader("Authorization", "Bearer "+GITHUB_TOKEN)
				.exchangeStrategies(ExchangeStrategies
						.builder()
						.codecs(codecs -> codecs
								.defaultCodecs()
								.maxInMemorySize(500 * 1024))
						.build())
				.build();
	}
}

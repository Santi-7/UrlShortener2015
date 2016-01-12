package urlshortener2015.candypink;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import urlshortener2015.candypink.auth.JWTokenFilter;
import urlshortener2015.candypink.auth.support.AuthURI;
import urlshortener2015.candypink.auth.support.AuthUtils;

/**
 * Main class for configure and run the application
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	/**
	 * Run the application
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Configure the application
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}

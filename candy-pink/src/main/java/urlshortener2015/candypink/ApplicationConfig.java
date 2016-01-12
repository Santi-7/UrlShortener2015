package urlshortener2015.candypink;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import urlshortener2015.candypink.auth.JWTokenFilter;
import urlshortener2015.candypink.auth.support.AuthURI;
import urlshortener2015.candypink.auth.support.AuthUtils;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import java.util.ArrayList;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import urlshortener2015.candypink.uploader.QueueObject;

/**
 * Application configuration
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
@Configuration
public class ApplicationConfig {

	// Key to encrypt JWT
	@Value("${jwt.key}")
	private String key;

	/**
	 * Creates a JWT filter
	 */
	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		JWTokenFilter authenticationFilter = new JWTokenFilter(key, AuthUtils.buildAuthURIs());
		registrationBean.setFilter(authenticationFilter);
		registrationBean.setUrlPatterns(AuthUtils.filterList());
		return registrationBean;
	}
	@Bean
	public LinkedBlockingQueue<QueueObject> csvQueue(){
		return new LinkedBlockingQueue<QueueObject>();
	}
}

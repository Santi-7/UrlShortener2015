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
 * Created by david on 2/01/16.
 */
@Configuration
public class ApplicationConfig {

	@Value("${jwt.key}")
	private String key;

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

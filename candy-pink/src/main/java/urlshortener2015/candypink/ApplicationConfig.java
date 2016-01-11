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
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Created by david on 2/01/16.
 */
@Configuration
@ComponentScan("checker")
@Import(checker.WebServiceConfig.class)
public class ApplicationConfig {

	@Value("${jwt.key}")
	private String key;

	/*@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		JWTokenFilter authenticationFilter = new JWTokenFilter(key, AuthUtils.buildAuthURIs());
		registrationBean.setFilter(authenticationFilter);
		return registrationBean;
	}*/

	@Bean
	public JavaMailSenderImpl javaMailSender(){

		JavaMailSenderImpl javaMailSender =  new JavaMailSenderImpl();
		/*javaMailSender.setHost("smtp.gmail.com");
		javaMailSender.setPort(587);
		javaMailSender.setUsername("nicu1309@gmail.com");
		javaMailSender.setPassword("Molondrin");*/
		Properties props = new Properties();
		props.put("mail.smtp.user","nicu1309@gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.EnableSSL.enable","true");
		javaMailSender.setJavaMailProperties(props);
		return javaMailSender;
	}
}

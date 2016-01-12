package urlshortener2015.candypink.checker;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


@EnableWs
@Configuration
@EnableAsync
@EnableScheduling
/**
 * Configuration of the web service
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
public class WebServiceConfig extends WsConfigurerAdapter {

	/**
	 * Initializes the context of the web service
	 * @param applicationContext
	 *                          Context of the spring application
	 * @return Servlet that maps the web service
	 *
	 */
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	/**
	 * Generates the wsdl definition from the schema provided
	 * @param countriesSchema;
	 *                       Base schema for generating wsdl
	 * @return wsdl definition
	 */
	@Bean(name = "checker")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CheckerPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://urlshortener2015/candypink/checker/web/ws/schema");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	/**
	 * Parser for the base schema
	 * @return	Object that is interpreted as xsd schema
	 */
	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("checker.xsd"));
	}

	/**
	 * Bean definition for the shared queue of urls
	 * @return Singleton blocking queue
	 */
	@Bean
	public BlockingQueue<String> sharedQueue(){
		return new LinkedBlockingQueue<String>();
	}
}

package urlshortener2015.candypink.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class CandyMailConfig{

    private static final Logger log = LoggerFactory
            .getLogger(CandyMailConfig.class);

    @Value("${email.port}")
    private int port;
    @Value("${email.username}")
    private String username;
    @Value("${email.host}")
    private String host;
    @Value("${email.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl javaMailSender =   new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setPassword(password);
        javaMailSender.setUsername(username);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        javaMailSender.setJavaMailProperties(props);
        log.info(javaMailSender.getJavaMailProperties().toString());
        return javaMailSender;
    }
}

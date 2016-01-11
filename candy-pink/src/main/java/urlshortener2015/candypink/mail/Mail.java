package urlshortener2015.candypink.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;  
       
public class Mail{  
    	
	@Autowired
	private JavaMailSenderImpl mailSender;  
       
        public void sendMail(String from, String to, String subject, String msg) {  
            //creating message  
            SimpleMailMessage message = new SimpleMailMessage();  
            message.setFrom(from);  
            message.setTo(to);  
            message.setSubject(subject);  
            message.setText(msg);  
            //sending message  
            mailSender.send(message);     
        }  
    }  

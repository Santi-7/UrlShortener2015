package urlshortener2015.candypink.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import urlshortener2015.candypink.uploader.QueueObject;
import urlshortener2015.candypink.uploader.HtmlFormInfo;

import urlshortener2015.candypink.auth.support.AuthUtils;

import io.jsonwebtoken.*;

/**
 * This controller manages the incoming messages from the clients
 * sent vía websockets.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa  
 */
@Controller
public class WebSocketController {
	
	@Value("${jwt.key}")
	private String key;
	
	private final SimpMessagingTemplate messagingTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
	
	@Resource
	protected LinkedBlockingQueue<QueueObject> csvQueue;
	
	@Autowired
	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	/**
	 * Handles every messages sent to "/uploader". Constructs
	 * a new QueueObject with the information received
	 * and puts it into the queue.
	 * @param hf - JSON with information about the URL to short, the token
	 * from the users session and the queue on which the client is subscribed.
	 */
    @MessageMapping("/uploader")
    public void handleFormUpload(HtmlFormInfo hf){
		try{
			if(!(hf.getUrl()).equals("")){
				Claims claims = AuthUtils.getClaims(hf.getToken(), key);
				QueueObject qo = new QueueObject(hf.getUrl(), "url", claims.getSubject(), claims.get("role", String.class));
				qo.setUri(hf.getSubs());
				csvQueue.put(qo);
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
}

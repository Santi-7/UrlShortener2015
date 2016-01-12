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

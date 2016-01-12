/*package urlshortener2015.candypink.web;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener2015.candypink.uploader.UpdateMessage;

@Controller
public class WebSocketController {

    private SimpMessagingTemplate template;
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Scheduled(fixedDelay = 10000)
    public void greet() {
			logger.info("***HASTA AQUI LLEGO22");
        this.template.convertAndSendToUser("user", "/queue/urlUploads", "MENSAJE DE PRUEBA");
    }
}*/

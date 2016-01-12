package urlshortener2015.candypink.web;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import urlshortener2015.candypink.uploader.QueueObject;
import urlshortener2015.candypink.uploader.QueueInfo;


@Controller
public class WebSocketController {

    @MessageMapping("/uploader")
    @SendTo("/queue/response")
    public QueueInfo handleFormUpload(String url, HttpServletRequest request,
				HttpServletResponse response){
		
		if(!url.equals("")){
			QueueObject qo = new QueueObject(url, "url", null, null);
			qo.setUri("/queue/urlUploads/user"+qo.hashCode());
			QueueInfo qi = new QueueInfo(qo.getUri());
			return qi;
		}
		return null;
	}
}

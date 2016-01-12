package urlshortener2015.candypink.uploader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;

import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerRequest;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerResponse;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;
import urlshortener2015.candypink.web.UrlShortenerController;
import urlshortener2015.candypink.web.shortTools.Short;

import io.jsonwebtoken.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Random;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CsvQueueConsumerBean{
	
	private int procNum;
	
	@Resource
	protected LinkedBlockingQueue<QueueObject> csvQueue;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(CsvQueueConsumerBean.class);
	
	private Jaxb2Marshaller marshaller;//Communication to WS
	
	private final SimpMessagingTemplate messagingTemplate;
	
	@Autowired
	public CsvQueueConsumerBean(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	@Async
	public void test(){
		while(true){
			clientUpdate(null);
		}
	}
	
	@Async
	public void extractAndProcess(){
		try{
			while(true){
				QueueObject qo = csvQueue.take();
				Object obj = qo.getToShort();
				if(obj instanceof MultipartFile){
					processFile(qo);
				}
				else{
					processUrl(qo);
				}
			}
		}
		catch(InterruptedException e){
			System.err.println(e);
		}
		catch (Exception e){
			System.err.println(e);
		}
	}
    
	
	@PostConstruct
	private void initWsComs(){
		marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCheckerRequest.class));
		try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {}
	}
	
	private void copyFile(MultipartFile f) throws Exception{
		
		byte[] bytes = f.getBytes();
			BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(new File("temp")));
			stream.write(bytes);
			stream.close();	
	}
	
	
	public void clientUpdate(UpdateMessage update) {
        this.messagingTemplate.convertAndSend(update.getUser(), update.getStatus());
        logger.info("***ENVIO MENSAJE a: "+update.getUser());
    }
    
    private void processFile(QueueObject qo){
		try{
			copyFile((MultipartFile) qo.getToShort());
			File f = new File("temp");
			Scanner sc = new Scanner(f);
			sc.useDelimiter(",|\\s");
			Client client = ClientBuilder.newClient();
			while(sc.hasNext()){
				String url = sc.next();
				ResponseEntity<ShortURL> res = Short.shortsFromUploader(url, qo.getUsername(), qo.getRole(), shortURLRepository, marshaller);
				if(res!=null && ((res.getStatusCode()).toString()).equals("400")){
					String stat = url + " : Failed";
					UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
					clientUpdate(um);
				}
				else{
					String stat = url + " -> " + (res.getBody().getUri()) + " : Success";
					UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
					clientUpdate(um);
				}
			}
			f.delete();
		}
		catch(FileNotFoundException e){
			System.err.println(e);
		}
		catch(Exception e){
			System.err.println(e);
		}
		
	}
    
    private void processUrl(QueueObject qo){
		String url = (String) qo.getToShort();
		ResponseEntity<ShortURL> res = Short.shortsFromUploader(url, qo.getUsername(), qo.getRole(), shortURLRepository, marshaller);
		if(res!=null && ((res.getStatusCode()).toString()).equals("400")){
			String stat = url + " : Failed";
			UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
			clientUpdate(um);
		}
		else{
			String stat = url + " -> " + (res.getBody().getUri()) + " : Success";
			UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
			clientUpdate(um);
		}
	}	
}

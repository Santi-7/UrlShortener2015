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

/**
 * This class provides an asynchronous method to consume
 * the queue of CSVs and URLs and processing and shortening
 * them and sending the information of the process to the
 * client.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@Component
public class CsvQueueConsumerBean{
	
	@Resource
	protected LinkedBlockingQueue<QueueObject> csvQueue;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(CsvQueueConsumerBean.class);
	
	private Jaxb2Marshaller marshaller;//Communication to WS
	
	private final SimpMessagingTemplate messagingTemplate;
	
	/**
	 * Initialize the template used to send messages via websockets.
	 */ 
	@Autowired
	public CsvQueueConsumerBean(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	/**
	 * An asynchronous method that takes objects form the
	 * queue and calls the appropiate method based on the
	 * content of the object.
	 */ 
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
    
	/**
    * Initializes the utils to communicate with the Web Service
    */
	@PostConstruct
	private void initWsComs(){
		marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCheckerRequest.class));
		try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {}
	}
	
	/**
	 * Copies the Multipart File uploaded to the server to a 
	 * standard temporal file in the server.
	 * @param f - The file to copy.
	 */ 
	private void copyFile(MultipartFile f) throws Exception{
		
		byte[] bytes = f.getBytes();
			BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(new File("temp")));
			stream.write(bytes);
			stream.close();	
	}
	
	/**
	 * Sends a message to the client with the information
	 * about the shortening process (either success or failed).
	 * @param update - Object containing the information to send
	 * and where to send it.
	 */ 
	private void clientUpdate(UpdateMessage update) {
        this.messagingTemplate.convertAndSend(update.getUser(), update.getStatus());
    }
    
    /**
     * Process the CSV file extracting the URLs on it,
     * shortening them and sending a message with the
     * information on the process to the client.
     * @param qo - The object with all the information needed
     * to short the URLs on it.
     */ 
    private void processFile(QueueObject qo){
		try{
			//Copies the MultipartFile to a standard file.
			copyFile((MultipartFile) qo.getToShort());
			File f = new File("temp");
			Scanner sc = new Scanner(f);
			sc.useDelimiter(",|\\s");
			Client client = ClientBuilder.newClient();
			//Reads the file.
			while(sc.hasNext()){
				String url = sc.next();
				//Shorts the read URL.
				ResponseEntity<ShortURL> res = Short.shortsFromUploader(url, qo.getUsername(), qo.getRole(), shortURLRepository, marshaller);
				//If the shortening process failed.
				if(res!=null && ((res.getStatusCode()).toString()).equals("400")){
					String stat = url + " : Failed";
					UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
					clientUpdate(um);
				}
				//If the shortening process went well.
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
    
    /**
     * Shorts the URL contained on the QueueObject
     * and sends a message to the client with
     * information on the process.
     */ 
    private void processUrl(QueueObject qo){
		String url = (String) qo.getToShort();
		//Shorts the read URL.
		ResponseEntity<ShortURL> res = Short.shortsFromUploader(url, qo.getUsername(), qo.getRole(), shortURLRepository, marshaller);
		//If the shortening process failed.
		if(res!=null && ((res.getStatusCode()).toString()).equals("400")){
			String stat = url + " : Failed";
			UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
			clientUpdate(um);
		}
		//If the shortening process went well.
		else{
			String stat = url + " -> " + (res.getBody().getUri()) + " : Success";
			UpdateMessage um = new UpdateMessage(stat, qo.getUri());  
			clientUpdate(um);
		}
	}	
}

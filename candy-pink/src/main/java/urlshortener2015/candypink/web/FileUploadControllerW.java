package urlshortener2015.candypink.web;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import urlshortener2015.candypink.repository.ShortURLRepository;
import urlshortener2015.candypink.uploader.QueueInfo;
import urlshortener2015.candypink.uploader.QueueObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This controller manages incoming requests from the clients for
 * uploading a file to the server.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa  
 */
@RestController
@RequestMapping("/upload")
public class FileUploadControllerW {
	
	@Resource
	protected LinkedBlockingQueue<QueueObject> csvQueue;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;

	/**
	 * If a get request is received, it informs the user that
	 * he can upload a file doing a post request to this 
	 * address.
	 */ 
    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }
	
	/**
	 * Handles every post request sent to this address in order
	 * to upload a file to the server. The file will be put on a queue
	 * to be processed as soon as possible.
	 * @param file - The file uploaded.
	 * @param request - The request of the client.
	 * @return The information with the queue on which the client
	 * will have to subscribe.
	 */
    @RequestMapping(method=RequestMethod.POST)
    public QueueInfo handleFileUpload(@RequestParam("file") MultipartFile file, 
				HttpServletRequest request){
	
		final Claims claims = (Claims) request.getAttribute("claims");	
		// Obtain username
		String username = claims.getSubject(); 
		// Obtain role
		String role = claims.get("role", String.class);			
        if (!file.isEmpty()) {
			try{
				/*
				 * Constructs a new QueueObject with the file uploaded, 
				 * the username, the role of the user and the queue on
				 * which the client will be subscribed.
				 */ 
				QueueObject qo = new QueueObject(file, "csv", username, role);
				qo.setUri("/queue/urlUploads/user"+qo.hashCode());
				//Puts the objetct on the queue.
				csvQueue.put(qo);
				/*
				 * Creates a new QueueInfo with the queue on which the
				 * client will have to subscribe.
				 */ 
				QueueInfo qi = new QueueInfo(qo.getUri());
				return qi;
			}
			catch(InterruptedException e){
				System.err.println(e);
			}
		}
		return null;
    }

}

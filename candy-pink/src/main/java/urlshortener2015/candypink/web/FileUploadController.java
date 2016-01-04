package urlshortener2015.candypink.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import urlshortener2015.candypink.domain.csvStatusInfo;
import urlshortener2015.candypink.domain.ShortURL;

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

import checker.web.ws.schema.GetCheckerRequest;
import checker.web.ws.schema.GetCheckerResponse;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Random;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn; 

@RestController
@RequestMapping("/upload")
public class FileUploadController {
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
	
	private Jaxb2Marshaller marshaller;//Communication to WS

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(method=RequestMethod.POST)
    public csvStatusInfo handleFileUpload(@RequestParam("file") MultipartFile file, 
				HttpServletRequest request){
		csvStatusInfo inf = null;
        if (!file.isEmpty()) {
			try{
				copyFile(file);
				File f = new File("temp");
				boolean ok = true;
				inf = new csvStatusInfo();
				Scanner sc = new Scanner(f);
				sc.useDelimiter(",|\\s");
				while(sc.hasNext() && ok){
					String url = sc.next();
					logger.info(url);
					ResponseEntity<ShortURL> res = shortener(url, null, null, null, null, request);
					if(res!=null && ((res.getStatusCode()).toString()).equals("400")){  
						ok=false;
						inf.setFailed(url);
					}
					else{ inf.add("Success: "+(res.getBody()).getUri()); }
				}
				f.delete();
			}
			catch (FileNotFoundException e){
				System.err.println(e);
			}
			catch (IOException e){
				System.err.println(e);
			}
			catch (Exception e){
				System.err.println(e);
			}
		}
		return inf;    
    }
    
	private ResponseEntity<ShortURL> shortener(String url, String users, String time,
			String sponsor, String brand, HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		logger.info("Users who can redirect: " + users);
		logger.info("Time to be safe: " + time);
		Client client = ClientBuilder.newClient();
		boolean safe = false;
		if(users!=null && time!=null){ safe = !(users.equals("select") && time.equals("select"));}
		ShortURL su = createAndSaveIfValid(url, safe, sponsor, brand, UUID

			.randomUUID().toString(), extractIP(request));
		if (su != null) {
			if (su.getSafe() == false) {// Url requested is not safe
				HttpHeaders h = new HttpHeaders();
				h.setLocation(su.getUri());
				logger.info("Requesting to Checker service");
				GetCheckerRequest requestToWs = new GetCheckerRequest();
				requestToWs.setUrl(url);
				Object response = new WebServiceTemplate(marshaller).marshalSendAndReceive("http://localhost:"
						+ "8080" + "/ws", requestToWs);
				GetCheckerResponse checkerResponse = (GetCheckerResponse) response;
				String resultCode = checkerResponse.getResultCode();
				logger.info("respuesta recibida por el Web Service: "+resultCode);
				if(resultCode.equals("ok")){
					return new ResponseEntity<ShortURL>(su, h, HttpStatus.CREATED);
				}else{
					return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return null;
			}
		} else {
			return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
		}
	}
    
    
	protected ShortURL createAndSaveIfValid(String url, boolean safe, String users,
			String sponsor,	String brand, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			String id = Hashing.murmur3_32()
					.hashString(url, StandardCharsets.UTF_8).toString();
			String token = null;
			// If Url is safe, we create the token, else token = null
			if (safe == true) {
				// Random token of ten digits
				token = createToken(10);
			}
			// ShortUrl
			ShortURL su = null;
			try {
				su = new ShortURL(id, url,
					linkTo(
						methodOn(UrlShortenerController.class).redirectTo(
							id, token, null, null)).toUri(), token, users,
							sponsor, new Date(System.currentTimeMillis()),
							owner, HttpStatus.TEMPORARY_REDIRECT.value(),
							safe, null,null,null, null, ip, null, null);
			}
			catch (IOException e) {}
			if (su != null) {
					return shortURLRepository.save(su);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a random token of digits
	 * @param length - length of the token to return
	 */
	private String createToken(int length) {
		Random r = new Random();
		String token = "";
		for (int i = 0; i < length; i++) {
			// Only digits in the token
			token += r.nextInt(10);
		}
		return token;
	}
	
	protected String extractIP(HttpServletRequest request) {
		return request.getRemoteAddr();
	}
	
	private void copyFile(MultipartFile f) throws Exception{
		
		byte[] bytes = f.getBytes();
			BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(new File("temp")));
			stream.write(bytes);
			stream.close();	
	}
	
	@PostConstruct
	private void initWsComs(){
		marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCheckerRequest.class));
		try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {}
	}
}

package urlshortener2015.candypink.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;
import java.io.IOException;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

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
import urlshortener2015.candypink.uploader.QueueObject;
import urlshortener2015.candypink.uploader.QueueInfo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

import io.jsonwebtoken.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn; 

@RestController
@RequestMapping("/upload")
public class FileUploadControllerW {
	
	@Resource
	protected LinkedBlockingQueue<QueueObject> csvQueue;
	
	@Autowired
	protected ShortURLRepository shortURLRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
	
	private Jaxb2Marshaller marshaller;//Communication to WS

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(method=RequestMethod.POST)
    public QueueInfo handleFileUpload(@RequestParam("file") MultipartFile file, 
				HttpServletRequest request, HttpServletResponse response){
	
		final Claims claims = (Claims) request.getAttribute("claims");	
		// Obtain username
		String username = claims.getSubject(); 
		// Obtain role
		String role = claims.get("role", String.class);			
        if (!file.isEmpty()) {
			try{
				QueueObject qo = new QueueObject(file, "csv", username, role);
				qo.setUri("/queue/urlUploads/user"+qo.hashCode());
				csvQueue.put(qo);
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

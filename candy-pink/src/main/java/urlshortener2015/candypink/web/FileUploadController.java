package urlshortener2015.candypink.web;


import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerRequest;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerResponse;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.client.core.WebServiceTemplate;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.uploader.CsvStatusInfo;
import urlshortener2015.candypink.repository.ShortURLRepository;

import urlshortener2015.candypink.web.shortTools.Short;
import urlshortener2015.candypink.web.shortTools.Redirect;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Random;
import java.util.Scanner;
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
    public CsvStatusInfo handleFileUpload(@RequestParam("file") MultipartFile file, 
				HttpServletRequest request, HttpServletResponse response){
		CsvStatusInfo inf = null;
        if (!file.isEmpty()) {
			try{
				copyFile(file);
				File f = new File("temp");
				boolean ok = true;
				inf = new CsvStatusInfo();
				Scanner sc = new Scanner(f);
				sc.useDelimiter(",|\\s");
				while(sc.hasNext() && ok){
					String url = sc.next();
					logger.info(url);
					ResponseEntity<ShortURL> res = Short.shorts(url, null, null, null, null, request, response, 										      shortURLRepository, marshaller);
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

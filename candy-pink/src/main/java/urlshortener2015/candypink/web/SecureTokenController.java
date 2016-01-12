package urlshortener2015.candypink.web;


import java.util.Date;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import urlshortener2015.candypink.domain.User;
import urlshortener2015.candypink.repository.SecureTokenRepository;
import urlshortener2015.candypink.repository.SecureTokenRepositoryImpl;
import urlshortener2015.candypink.auth.support.AuthUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Base64Utils;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import java.util.Random;
import urlshortener2015.candypink.domain.SecureToken;

/**
 * This controller manage the access to "/secure" used for a user when 
 *  wants to get a secure token.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
@RestController
@RequestMapping("/secure")
public class SecureTokenController {

	// logger 
	private static final Logger logger = LoggerFactory.getLogger(SecureTokenController.class);

	// secure token repository
	@Autowired
	protected SecureTokenRepository secureTokenRepository;

	// shortURLRepository
	@Autowired
        protected ShortURLRepository shortURLRepository;

	/**
	 * 
	 * @param uri - URI for that is the secure token
	 * @param token - Token for the uri
	 * @param request - Request of the client
	 * @param response - Response to the request of the client
	 * @return The token which has been created
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> produceToken(@RequestParam("uri") String uri, @RequestParam("token") String token, 						  HttpServletRequest request, HttpServletResponse response) {
		logger.info("Request a secure token"); 
		logger.info("Uri: " + uri);
		logger.info("Token: " + token);
		// Search the uri
		ShortURL l = shortURLRepository.findByKey(uri);
		// The uri exist and the token is correct		
		if(l != null && l.getToken().equals(token)) {
			logger.info("The uri exist and token is correct");
			// Generate the secure token
			String scureToken = createToken(10).concat(token);
			try{
				// Encrypt the token
				String crypt = Base64Utils.encodeToString(scureToken.getBytes("UTF-8"));
				// Save the token
				if(secureTokenRepository.save(new SecureToken(crypt))!=null) {
					logger.info("Token saved");
					return new ResponseEntity<>(scureToken, HttpStatus.OK);			
				}
			}
			// Exception occurred
			catch(Exception e){
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);				
			}
		}
		// The user don't exist or the token is incorrect
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);			
	}

   	/**
       	 * Creates a random token of digits
     	 * @param length - length of the token to return
	 * @return random token compose of digits
     	 */
    	private String createToken(int length) {
        	Random r = new Random();
        	String token = "";
        	for (int i = 0; i < length; i++) {
            		token += r.nextInt(10);
        	}
        	return token;
    	}

       /**
	 * Return true if id and password are not empty. Return false otherwise.
	 * @param id - Id to verify.
	 * @param password - Paswword to verify.
	 * @return Return true if id and password are not empty. Return false otherwise.
	 */
	private boolean verifyFields(String id, String password) {
		// Check id
	  	if (id == null || id.isEmpty()) {
	  		return false;
	  	}
	  	// Check password
	  	else if (password == null || password.isEmpty()) {
	  		return false;
	  	}
	  	return true;
	}
}

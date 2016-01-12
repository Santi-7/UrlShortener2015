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

@RestController
@RequestMapping("/secure")
public class SecureTokenController {

	private static final Logger logger = LoggerFactory.getLogger(SecureTokenController.class);

	@Autowired
	protected SecureTokenRepository secureTokenRepository;

	@Autowired
        protected ShortURLRepository shortURLRepository;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> login(@RequestParam("uri") String uri, @RequestParam("token") String token, 						  HttpServletRequest request, HttpServletResponse response) {
		logger.info("Petición de token de seguridad"); 
		logger.info("Uri: " + uri);
		logger.info("Token: " + token);
		ShortURL l = shortURLRepository.findByKey(uri);
		if(l != null && l.getToken().equals(token)) {
			logger.info("Existe y esta bien el token");
			String scureToken = createToken(10).concat(token);
			try{
				String crypt = Base64Utils.encodeToString(scureToken.getBytes("UTF-8"));
				if(secureTokenRepository.save(new SecureToken(crypt))!=null) {
					logger.info("Token guardado");
					return new ResponseEntity<>(scureToken, HttpStatus.OK);			
				}
			}
			catch(Exception e){
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);				
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);			
	}

   /**
     * Creates a random token of digits
     *
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

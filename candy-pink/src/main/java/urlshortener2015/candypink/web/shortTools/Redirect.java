package urlshortener2015.candypink.web.shortTools;

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
import org.springframework.ws.client.core.WebServiceTemplate;
import urlshortener2015.candypink.domain.FishyURL;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;
import io.jsonwebtoken.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Random;
import java.util.UUID;

import urlshortener2015.candypink.repository.SecureTokenRepository;
import urlshortener2015.candypink.repository.SecureTokenRepositoryImpl;
import urlshortener2015.candypink.domain.SecureToken;

import org.springframework.util.Base64Utils;

/**
 * This class offers method for redirect a shortURL
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
public class Redirect {

    // Message if is spam
    public static final String IS_SPAM = "is_spam";
    // Message if is not reachable
    public static final String NOT_REACH = "not_reach";
    // Message if is not authorizated
    public static final String NOT_AUTH = "not_auth";
    // Message if not exist
    public static final String NOT_EXISTS = "not_exists";
    // OK message
    public static final String OK = "ok";
    // Message if not match
    public static final String NOT_MATCH = "not_match";
    // Message if the uri is spam
    public static final String MESSAGE_SPAM = "This url is classified as malware or spam according " +
            "to Google Safe Browsing service. ";
    // Message if the token is incorrect
    public static final String INCORRECT_TOKEN_PAGE = "incorrectToken.html";
    // Mensaje if is no authorizated
    public static final String AUTH_REQUIRED_PAGE = "403.html";

    // logger
    private static final Logger logger = LoggerFactory.getLogger(Redirect.class);


    /**
     * Creates a successful reditection for a uri
     * @param l - URI which must be redirected
     * @returns redirection for the uri
     */
    public static ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }
   
   /*
    * Validates a uri and return the checking code
    * @param l - URI to validate
    * @param token - Token for the access to the URI
    * @param secureToken - SecureToken for the access to the URI
    * @param request - Request od the client
    * @param secureTokenRepository - Repository of secure tokens
    * @return representation code of evaluate url
    * */
    public static String validateURI(ShortURL l, String token, String secureToken, HttpServletRequest request,
			       SecureTokenRepository secureTokenRepository) {
        // ShortUrl exists in our BBDD
        if (l != null) {
            // URL is not spam, is reachable and is enabled
            if (!l.getSpam() && l.getReachable() && l.getEnabled()) {
                // URL is safe, we must check token
                logger.info("Is URL safe?: " + l.getSafe());
                if (l.getSafe()) {
                    logger.info("Client token " + token + " - Real token: " + l.getToken());
                    // Token doesn't match
                    if (!l.getToken().equals(token)) {
                        return NOT_MATCH;
                    }
		    // There are a secure token and is valid
		    logger.info("Verify Token");
		    if(secureToken != null && verifyToken(secureToken, token, secureTokenRepository)) {
			    //Needed permission
			    if (!l.getUsers().equals("All")) {
			        // Obtain jwt
			        final Claims claims = (Claims) request.getAttribute("claims");
			        try {
			            // Obtain username
			            String username = claims.getSubject();
			            // Obtain role
			            String role = claims.get("role", String.class);
                                    // The user haven't got permission
			            if ((l.getUsers().equals("Premium") && !role.equals("ROLE_PREMIUM")) ||
			                    (l.getUsers().equals("Normal") && !role.equals("ROLE_NORMAL"))) {
			                return NOT_AUTH;
			            }
				// A exception occurred
			        } catch (NullPointerException e) {
			            return NOT_AUTH;
			        }
			    }
		  // URL is not safe or token matches
		  logger.info("Devuelve OK");
		  return OK;
		  }
		  logger.info("Secure Token bad verified");
		  return NOT_AUTH;      
	}
				return OK;
            }else if(!l.getReachable()){
                //Uri is not reachable
                return NOT_REACH;
            }else if(!l.getEnabled()){
				//Uri is not enabled
				return NOT_EXISTS;
			}
            else {
                // URL is spam
                return IS_SPAM;
            }
            // ShortUrl does not exist in our BBDD
        } else {
            return NOT_EXISTS;
        }
    }

     /**
      * Verify the secureToken of the URI
      * @param secureToken - Secure token of the URI
      * @param token - Token of the URI
      * @param secureTokenRepository - Repository of secure tokens
      * @return true if the token is valid, false in other case
      */
     private static boolean verifyToken(String secureToken, String token, SecureTokenRepository secureTokenRepository) {
	SecureToken st = secureTokenRepository.findByToken(secureToken);
	// The token exist
	if(st != null) {
		logger.info("The token exist");
		try {
			// Encrypt the token
			String crypt = new String(Base64Utils.decodeFromString(st.getToken()), "UTF-8");
			logger.info("Obtenido: " + crypt);
			// Obtain the key
			String key = crypt.substring(crypt.length()-token.length());
			logger.info("Salt:" + key + "-" + "Token: " + token);
			// The key is correct
			if(key.equals(token)) {
				// Delete the token
				return secureTokenRepository.delete(secureToken);
			}
		}
		// An exception occurred
		catch(Exception e) {
			return false;		
		}
	}
	logger.info("The token not exists");
	return false;	
    }
}

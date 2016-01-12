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

public class Redirect {

    public static final String IS_SPAM = "is_spam";
    public static final String NOT_REACH = "not_reach";
    public static final String NOT_AUTH = "not_auth";
    public static final String NOT_EXISTS = "not_exists";
    public static final String OK = "ok";
    public static final String NOT_MATCH = "not_match";
    public static final String MESSAGE_SPAM = "This url is classified as malware or spam according " +
            "to Google Safe Browsing service. ";
    public static final String INCORRECT_TOKEN_PAGE = "incorrectToken.html";
    public static final String AUTH_REQUIRED_PAGE = "403.html";

    public static final Logger logger = LoggerFactory.getLogger(Redirect.class);


    public static ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }
/*
    * Validates the uri
    * @return representation code of evaluate url
    * */
    public static String validateURI(ShortURL l, String token, String secureToken, HttpServletRequest request,
			       SecureTokenRepository secureTokenRepository) {
        // ShortUrl exists in our BBDD
        if (l != null) {
            // URL is not spam
            if (!l.getSpam() && l.getReachable() && l.getEnabled()) {
                // URL is safe, we must check token
                logger.info("Is URL safe?: " + l.getSafe());
                if (l.getSafe()) {
                    logger.info("Client token " + token + " - Real token: " + l.getToken());
                    // Token doesn't match
                    if (!l.getToken().equals(token)) {
                        return NOT_MATCH;
                    }
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
						            if ((l.getUsers().equals("Premium") && !role.equals("ROLE_PREMIUM")) ||
						                    (l.getUsers().equals("Normal") && !role.equals("ROLE_NORMAL"))) {
						                return NOT_AUTH;
						            }
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
      *
      */
     private static boolean verifyToken(String secureToken, String token, SecureTokenRepository secureTokenRepository) {
	SecureToken st = secureTokenRepository.findByToken(secureToken);
	if(st != null) {
		logger.info("El token existe");
		try {
			String crypt = new String(Base64Utils.decodeFromString(st.getToken()), "UTF-8");
			logger.info("Obtenido: " + crypt);
			String salt = crypt.substring(crypt.length()-token.length());
			logger.info("Salt:" + salt + "-" + "Token: " + token);
			if(salt.equals(token)) {
				secureTokenRepository.delete(secureToken);
				return true;
			}
		}
		catch(Exception e) {
			logger.info("EL token no existe");
			return false;		
		}
	}
	logger.info("El token no existe");
	return false;	
    }
}

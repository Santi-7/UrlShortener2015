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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);
    private static final String IS_SPAM = "is_spam";
    private static final String NOT_REACH = "not_reach";
    private static final String NOT_AUTH = "not_auth";
    private static final String NOT_EXISTS = "not_exists";
    private static final String OK = "ok";
    private static final String NOT_MATCH = "not_match";
    private static final String MESSAGE_SPAM = "This url is classified as malware or spam according " +
            "to Google Safe Browsing service. ";
    private static final String INCORRECT_TOKEN_PAGE = "incorrectToken.html";
    private static final String AUTH_REQUIRED_PAGE = "403.html";

    private Jaxb2Marshaller marshaller;//Communication to WS


    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected SecureTokenRepository secureTokenRepository;


    /**
     * Redirect to the related URL associated to the ShortUrl with hash id
     * If URL is spam returns the JSON representation of a FishyURL as response
     * If URL is safe and token doesn't match, it is redirected to incorrectToken.html
     *
     * @param id    - hash of the shortUrl
     * @param token - optional, token of the shorturl if it is safe
     */
    @RequestMapping(value = "/{id:(?!link|index|login|signUp|profile|admin|incorrectToken|uploader|errorSpam|noMore|403|fishyURL).*}",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON})
    public ResponseEntity<?> redirectToJSON(@PathVariable String id,
                                            @RequestParam(value = "token", required = false) String token,
					    @RequestParam(value = "secure", required = false) String secureToken,
                                            HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("Requested redirectionJSON with hash " + id);
        ShortURL l = shortURLRepository.findByKey(id);
        String code = validateURI(l, token, secureToken, request);
        if (code.equals(IS_SPAM)) {
            //URL is spam or malware
            FishyURL fishyURL = new FishyURL(l.getTarget(), l.getSpamDate().toString(), MESSAGE_SPAM);
            return new ResponseEntity<>(fishyURL, HttpStatus.OK);
        } else {
            return generateResponse(l, code, response, request);

        }
    }
    /**
     * Redirect to the related URL associated to the ShortUrl with hash id
     * If URL is spam returns the representation of a FishyURL as response
     * If URL is safe and token doesn't match, it is redirected to incorrectToken.html
     *
     * @param id    - hash of the shortUrl
     * @param token - optional, token of the shorturl if it is safe
     */
@RequestMapping(value = "/{id:(?!link|index|login|signUp|profile|admin|incorrectToken|uploader|errorSpam|noMore|403|fishyURL).*}",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_OCTET_STREAM})
    public ResponseEntity<?> redirectToAnything(@PathVariable String id,
                                                @RequestParam(value = "token", required = false) String token,
						@RequestParam(value = "secure", required = false) String secureToken,
                                                HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("Requested redirectionAnything with hash " + id);
        ShortURL l = shortURLRepository.findByKey(id);
        String code = validateURI(l, token, secureToken, request);
        if (code.equals(IS_SPAM)) {
            String content;
            if(l.getSpamDate() != null) {
                content = "<div align=\"center\"><h1>" +
                        "Error message: </h1><br>" + MESSAGE_SPAM +
                        "<br>However, you can go there under your own responsability.<br> " +
                        "Here you have the link: <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a>" +
                        "</div>";
            }else{
                content= "<div align=\"center\"><h1>" +
                        "Information</h1><br>" +
                        "Here is the link you have requested <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a><br>Oops! it seems we are busy and we have not verified this url yet, try it later :) " +
                        "</div>";
            }
            return new ResponseEntity<>(content,HttpStatus.TEMPORARY_REDIRECT);
        } else {
            return generateResponse(l,code, response, request);

        }
    }
    /**
     * Redirect to the related URL associated to the ShortUrl with hash id
     * If URL is spam returns the representation of a FishyURL as response
     * If URL is safe and token doesn't match, it is redirected to incorrectToken.html
     *
     * @param id    - hash of the shortUrl
     * @param token - optional, token of the shorturl if it is safe
     */
@RequestMapping(value = "/{id:(?!link|index|login|signUp|profile|admin|incorrectToken|uploader|errorSpam|noMore|403|fishyURL).*}",
            method = RequestMethod.GET, produces = {MediaType.TEXT_HTML})
    public ResponseEntity<?> redirectToHTML(@PathVariable String id,
                                                @RequestParam(value = "token", required = false) String token,
						@RequestParam(value = "secure", required = false) String secureToken,
                                                HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("Requested redirectionHTML with hash " + id);
        ShortURL l = shortURLRepository.findByKey(id);
        String code = validateURI(l, token, secureToken, request);
        if (code.equals(IS_SPAM)) {
            //URL is spam or malware
            String content;
            if(l.getSpamDate() != null) {
                content = "<div align=\"center\"><h1>" +
                        "Error message: </h1><br>" + MESSAGE_SPAM +
                        "<br>However, you can go there under your own responsability.<br> " +
                        "Here you have the link: <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a>" +
                        "</div>";
            }else{
                content= "<div align=\"center\"><h1>" +
                        "Information</h1><br>" +
                        "Here is the link you have requested <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a><br>Oops! it seems we are busy and we have not verified this url yet, try it later :) " +
                        "</div>";
            }
            return new ResponseEntity<>(content,HttpStatus.TEMPORARY_REDIRECT);
        } else {
            return generateResponse(l, code, response, request);

        }
    }

    protected String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }



    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "users", required = false) String users,
                                              @RequestParam(value = "time", required = false) String time,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("Requested new short for uri " + url);
        logger.info("Users who can redirect: " + users);
        logger.info("Time to be safe: " + time);
        // Obtain jwt
       final Claims claims = (Claims) request.getAttribute("claims");
       // Obtain username
        String username = claims.getSubject();
        // Obtain role
        String role = claims.get("role", String.class);
        if (role.equals("ROLE_NORMAL") && shortURLRepository.findByUserlast24h(username).size() >= 20) {
            logger.info("No more today");
            response.sendRedirect("noMore.html");
            // Can't redirect more today
            return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
        }
        boolean safe = !(users.equals("select") && time.equals("select"));
        if (users.equals("select")) {
            users = "All";
        }
        Integer timeToBeSafe;
        if (time.equals("select") || time.equals("Forever")) {
            timeToBeSafe = Integer.MAX_VALUE;
        }else if(time.equals("day")){
            timeToBeSafe = 1;
        }else if(time.equals("week")){
            timeToBeSafe = 7;
        }else{
            timeToBeSafe = 30;
        }
        ShortURL su = createAndSaveIfValid(url, username, safe, users, sponsor, brand, UUID
                .randomUUID().toString(), extractIP(request),timeToBeSafe);
        if (su != null) {
            HttpHeaders h = new HttpHeaders();
            h.setLocation(su.getUri());
            logger.info("Requesting to Checker service");
            GetCheckerRequest requestToWs = new GetCheckerRequest();
            requestToWs.setUrl(url);
            Object responseR = new WebServiceTemplate(marshaller).marshalSendAndReceive("http://localhost:"
                    + "8080" + "/ws", requestToWs);
            GetCheckerResponse checkerResponse = (GetCheckerResponse) responseR;
            String resultCode = checkerResponse.getResultCode();
            logger.info("respuesta recibida por el Web Service: " + resultCode);
            logger.info("Hay "+shortURLRepository.count()+ " urls en la bd");
            if (resultCode.equals("ok")) {
                return new ResponseEntity<ShortURL>(su, h, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.info("La uri es null");
            return new ResponseEntity<ShortURL>(HttpStatus.BAD_REQUEST);
        }
    }

    protected ShortURL createAndSaveIfValid(String url, String username, boolean safe, String users,
                                            String sponsor, String brand, String owner, String ip,
                                            Integer timeToBeSafe) {
        UrlValidator urlValidator = new UrlValidator(new String[]{"http",
                "https"});
        // It is a valid URL
        if (urlValidator.isValid(url)) {
            // Hash
            logger.info("La url es valida");
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
                                methodOn(UrlShortenerController.class).redirectToHTML(
                                        id, token, null, null, null)).toUri(), token, users,
                        sponsor, new Date(System.currentTimeMillis()),
                        owner, HttpStatus.TEMPORARY_REDIRECT.value(),
                        safe,timeToBeSafe, null, null, null, null, ip, null, username,
                        0,0,0,0);
                logger.info("Se ha creado la uri");
            } catch (IOException e) {
                logger.info("Ha surgido una ioexception en create and safeifvalid");
            }
            if (su != null) {
                logger.info("Se va a guardar en la bd");
                return shortURLRepository.save(su);
            } else {
                return null;
            }
            // It is not a valid URL
        } else {
            logger.info("No es una url valida");
            return null;
        }
    }

    /*
    * Initializes the utils to communicate with the Web Service
    * */
    @PostConstruct
    private void initWsComs() {
        marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan(ClassUtils.getPackageName(GetCheckerRequest.class));
        try {
            marshaller.afterPropertiesSet();
        } catch (Exception e) {
        }
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


    /*
    * Validates the uri
    * @return representation code of evaluate url
    * */
    private String validateURI(ShortURL l, String token, String secureToken, HttpServletRequest request) {
        // ShortUrl exists in our BBDD
        if (l != null) {
            // URL is not spam
            if (l.getSpam() == false && l.getReachable() == true) {
                // URL is safe, we must check token
                logger.info("Is URL safe?: " + l.getSafe());
                if (l.getSafe() == true) {
                    logger.info("Client token " + token + " - Real token: " + l.getToken());
                    // Token doesn't match
                    if (!l.getToken().equals(token)) {
                        return NOT_MATCH;
                    }
					logger.info("Verify Token");
					if(secureToken != null && verifyToken(secureToken, token)) {
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
            }else if(l.getReachable() == false){
                //Uri is not reachable
                return NOT_REACH;
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
     private boolean verifyToken(String secureToken, String token) {
	SecureToken st = secureTokenRepository.findByToken(secureToken);
	if(st != null) {
		logger.info("El token existe");
		try {
			String crypt = new String(Base64Utils.decodeFromString(st.getToken()), "UTF-8");
			logger.info("Obtenido: " + crypt);
			String salt = crypt.substring(crypt.length()-token.length());
			logger.info("Salt:" + salt + "-" + "Token: " + token);
			if(salt.equals(token)) {
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

    /*
    * Returns a JSON response according to the type of status code
    * */
    private ResponseEntity<?> generateResponse(ShortURL l, String code,
                                               HttpServletResponse response,
                                               HttpServletRequest request)
            throws IOException {
        if (code.equals(OK)) {
            //URL has no problems
            return createSuccessfulRedirectToResponse(l);
        } else if (code.equals(NOT_EXISTS)) {
            //URL does not exists
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (code.equals(NOT_MATCH)) {
            //Token is not correct
            response.sendRedirect(INCORRECT_TOKEN_PAGE);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (code.equals(NOT_AUTH)) {
            //Auth is needed to access the url
            response.sendRedirect(AUTH_REQUIRED_PAGE);
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        } else if(code.equals(NOT_REACH)){
            //Uri is not reachable, we redirect to message page
            String content;
            if(l.getReachableDate() != null) {
                content = "<div align=\"center\"><h1>" +
                        "Error</h1><br>" +
                        "Here is the link you have requested <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a><br>We are sorry, but it seems this url is not reachable since " + l.getReachableDate() +
                        "</div>";
            }else{
                content= "<div align=\"center\"><h1>" +
                        "Information</h1><br>" +
                        "Here is the link you have requested <a href=" + l.getTarget() + ">" + l.getTarget() +
                        "</a><br>Oops! it seems we are busy and we have not verified this url yet, try it later :) " +
                        "</div>";
            }
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(org.springframework.http.MediaType.TEXT_HTML);
            return new ResponseEntity<String>(content, responseHeaders, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

}

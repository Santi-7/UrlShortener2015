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

import urlshortener2015.candypink.web.shortTools.Short;
import urlshortener2015.candypink.web.shortTools.Redirect;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Random;
import java.util.UUID;

import urlshortener2015.candypink.repository.SecureTokenRepository;
import urlshortener2015.candypink.domain.SecureToken;

import org.springframework.util.Base64Utils;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerController.class);

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
        String code = Redirect.validateURI(l, token, secureToken, request, secureTokenRepository);
        if (code.equals(Redirect.IS_SPAM)) {
            //URL is spam or malware
            FishyURL fishyURL = new FishyURL(l.getTarget(), l.getSpamDate().toString(), Redirect.MESSAGE_SPAM);
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
            method = RequestMethod.GET)
    public ResponseEntity<?> redirectToAnything(@PathVariable String id,
                                                @RequestParam(value = "token", required = false) String token,
						@RequestParam(value = "secure", required = false) String secureToken,
                                                HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        logger.info("Requested redirectionAnything with hash " + id);
        ShortURL l = shortURLRepository.findByKey(id);
        String code = Redirect.validateURI(l, token, secureToken, request, secureTokenRepository);
        if (code.equals(Redirect.IS_SPAM)) {
            String content;
            if(l.getSpamDate() != null) {
                content = "<div align=\"center\"><h1>" +
                        "Error message: </h1><br>" + Redirect.MESSAGE_SPAM +
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
        String code = Redirect.validateURI(l, token, secureToken, request, secureTokenRepository);
        if (code.equals(Redirect.IS_SPAM)) {
            //URL is spam or malware
            String content;
            if(l.getSpamDate() != null) {
                content = "<div align=\"center\"><h1>" +
                        "Error message: </h1><br>" + Redirect.MESSAGE_SPAM +
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

    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "users", required = false) String users,
                                              @RequestParam(value = "time", required = false) String time,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest request, HttpServletResponse response)
            throws IOException {
       return Short.shorts(url, users, time, sponsor, brand, request, response, shortURLRepository, marshaller);
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

    /*
    * Returns a JSON response according to the type of status code
    * */
    private ResponseEntity<?> generateResponse(ShortURL l, String code,
                                               HttpServletResponse response,
                                               HttpServletRequest request)
            throws IOException {
        if (code.equals(Redirect.OK)) {
            //URL has no problems
            return Redirect.createSuccessfulRedirectToResponse(l);
        } else if (code.equals(Redirect.NOT_EXISTS)) {
            //URL does not exists
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (code.equals(Redirect.NOT_MATCH)) {
            //Token is not correct
            response.sendRedirect(Redirect.INCORRECT_TOKEN_PAGE);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else if (code.equals(Redirect.NOT_AUTH)) {
            //Auth is needed to access the url
            response.sendRedirect(Redirect.AUTH_REQUIRED_PAGE);
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        } else if(code.equals(Redirect.NOT_REACH)){
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

package urlshortener2015.candypink.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener2015.candypink.repository.ShortURLRepository;
import urlshortener2015.candypink.repository.ShortURLRepositoryImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import io.jsonwebtoken.*;

/**
* This class manages the profile of a user.
* It can only be accessed by the owner user of the information,
* and he must be logged in.
* It manages a get request to return all uris associated
* to that user, that have been realized last 24 hours.
* @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
*/
@RestController
@RequestMapping("/profile")
public class ProfileController {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	// Database
	@Autowired
	protected ShortURLRepository shortURLRepository;

	/** Default constructor of the controller */
	public ProfileController() {}

	/** Constructor with database "shortURLRepository" */
	public ProfileController(ShortURLRepositoryImpl shortURLRepository) {
		this.shortURLRepository = shortURLRepository;
  	}
  
  	/**
  	 * It returns all the uris associated to the user logged in, that
  	 * have been realized last 24 hours.
  	 */
  	@RequestMapping(method = RequestMethod.GET)
  	public ModelAndView getUrisFromUser(HttpServletRequest request) {
		// Obtain jwt
		final Claims claims = (Claims) request.getAttribute("claims");
		// Obtain username
		String username = claims.getSubject(); 
		logger.info("Requested profile from user " + username);
		ModelAndView model = new ModelAndView();
		// Add all the uris associated to the user realized last 24 hours
    		model.addObject("uris", shortURLRepository.findByUserlast24h(username));
    		// Redirection to profile Page
		model.setViewName("profilePage");
		return model;
  	}
}

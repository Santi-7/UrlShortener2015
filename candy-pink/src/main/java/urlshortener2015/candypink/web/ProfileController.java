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

@RestController
@RequestMapping("/profile")
public class ProfileController {

	private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

	@Autowired
	protected ShortURLRepository shortURLRepository;


	public ProfileController() {}

	public ProfileController(ShortURLRepositoryImpl shortURLRepository) {
		this.shortURLRepository = shortURLRepository;
  	}
  
  	@RequestMapping(method = RequestMethod.GET)
  	public ModelAndView getUrisFromUser(HttpServletRequest request) {
		// Obtain jwt
		final Claims claims = (Claims) request.getAttribute("claims");
		// Obtain username
		String username = claims.getSubject(); 
		// Obtain role
		//String role = claims.get("role", String.class);
		logger.info("Requested profile from user " + username);
		ModelAndView model = new ModelAndView();
    		model.addObject("Uris", shortURLRepository.findByUserlast24h(username));
		model.setViewName("profilePage.html");
		return model;
  	}
}

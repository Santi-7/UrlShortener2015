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
import urlshortener2015.candypink.repository.UserRepository;
import urlshortener2015.candypink.repository.UserRepositoryImpl;
import urlshortener2015.candypink.auth.support.AuthUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This controller manage the access to "/login" used for a user when he want log in.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
@RestController
@RequestMapping("/login")
public class LoginController {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	// Key of JWT
	@Value("${jwt.key}")
    	private String key;

	// User repository
	@Autowired
	protected UserRepository userRepository;


	/**
	 * Constructor of LoginController without params
	 */
	public LoginController() {}

	/**
	 * Constructor of LoginController with a user repository
	 * @param userRepository - Repository of users
	 */
	public LoginController(UserRepositoryImpl userRepository){
        	this.userRepository = userRepository;
    	}

	/**
	 * Return the model and view of login page
	 * @param error - error in log in
	 * @param request - request of the client
	 * @return the model and view of login page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("loginPage");
		return model;
	}

	/**
	 * Check the user whi has loged in exist in the database and give him a JWT
	 * @param id - username of the user
	 * @param password - password of the user
	 * @param request - request of the user
	 * @param response - response to the request of the user
	 * @returns the user who has loged in
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestParam("user") String id, @RequestParam("password") String password, 						  HttpServletRequest request, HttpServletResponse response) { 
		logger.info("Requested login with username " + id);
		//Verify the fields aren´t empty
		if (verifyFields(id, password)) {
			logger.info("Fields are not empty. User: "+ id + ",Pass: "+password);
			//There is a user with this username or email
			User user = userRepository.findByUsernameOrEmail(id);
			if (user != null) {
				logger.info("user is not null.  We found it");
				BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
				// The password is correct
				if(encoder.matches(password, user.getPassword())) {
					logger.info("Correct password");
					// Creates a token
					String token = AuthUtils.createToken(user.getUsername(), user.getAuthority(), 
							     key, new Date(System.currentTimeMillis() + 15*60*1000));
					logger.info("Token:" + token);
					// Put a token as a cookie of the response
					response.addCookie(new Cookie("Authorization", token));					
					// Return the user and a correct status
					logger.info("Devuelto");
					return new ResponseEntity<>(user, HttpStatus.CREATED);
				}
				// The password is incorrect
				else {
					// Return a bad request status
					logger.info("Not matches");
					logger.info("User pwd: "+ user.getPassword());
					logger.info("Pwd introduced: "+ password);
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			//There aren't a user with this username or email
			else {
				logger.info("No user with this username or email");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		// There are empty fields
		else {
			logger.info("Empty fields");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
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

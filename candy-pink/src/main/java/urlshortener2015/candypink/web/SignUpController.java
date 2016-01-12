package urlshortener2015.candypink.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;

/**
 * This class manages a sign up of a user
 * It manages a get request to redirect a user to the sign up page
 * and a post request to sign up a user who has introduced his details.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@RestController
@RequestMapping("/signUp")
public class SignUpController {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(SignUpController.class);

	// Database
	@Autowired
	protected UserRepository userRepository;

	/** Default constructor of the controller */
	public SignUpController() {}

	/** Constructor with database "shortURLRepository" */
	public SignUpController(UserRepositoryImpl userRepository){
        	this.userRepository = userRepository;
    	}

	/**
	 * It redirects the user to the signUp page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView register() {
		logger.info("Registry view requested");
		ModelAndView model = new ModelAndView();
		model.setViewName("signUpPage");
		return model;
	}
	
	/**
	 * It creates a user with the details passed by parameter and returns
	 * this user in JSON format and a CREATED http code.
	 * If any of the user information is null, it returns Bad Request code
	 * If there is already a user with same username or email,
	 * it returns Conflict code.
	 * @param username - username of the new user
	 * @param password - password of the new user
	 * @param email - email of the new user
	 * @param authority - authority (role) of the new user
	 * @param request - request of the client
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<User> register(@RequestParam("username") String username,
			        @RequestParam("password") String password, @RequestParam("email") String email,
			        @RequestParam("authority") String authority, HttpServletRequest request) {
		logger.info("Requested registration with username " + username + password + email + transform(authority));
		User user = new User(username, password, true, email, transform(authority));
		// Verify the fields aren´t empty
		if (verifyFields(user)) {
		  	logger.info("Requested registration verified");
		  	// There are users with the same username
		  	if(userRepository.findByUsernameOrEmail(username) != null) {
		    		return new ResponseEntity<>(HttpStatus.CONFLICT);
		  	}
			 // There are users with the same email
		  	else if(userRepository.findByUsernameOrEmail(email) != null) {
		    		return new ResponseEntity<>(HttpStatus.CONFLICT);
		  	}
		  	// There aren´t other users with this username or email
		  	else {
		    		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		    		user.setPassword(encoder.encode(password));
		    		logger.info("Requested registration correct");
		    		user = userRepository.save(user);
		    		logger.info("Requested registration done of user " + user.getUsername());
         			return new ResponseEntity<>(user, HttpStatus.CREATED);
			}
		}
		// There are empty fields
		else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

   	/**
	 * Return true if the user haven´t got empty fields. Return false otherwise.
	 * @param user - The user to verify.
	 * @return Return true if the user haven´t got empty fields. Return false otherwise.
	 */
	private boolean verifyFields(User user) {
		// Check username
	  	if(user.getUsername()==null || user.getUsername().isEmpty()) {
	  	  return false;
	  	}
	  	// Check password
	  	else if(user.getPassword()==null || user.getPassword().isEmpty()) {
	  	  return false;
	  	}
	  	// Check Enabled
	  	else if(user.getEnabled()==null || !user.getEnabled()) {
	  	  return false;
	  	}
	  	// Check email
	  	else if(user.getEmail()==null || user.getEmail().isEmpty()) {
	  	  return false;
	  	}
	  	// Check authority
	  	else if(user.getAuthority()==null || user.getAuthority().isEmpty()) {
	  	  return false;
	  	}
	  	return true;
	}
	
	/**
	 * Converts the role "role" to the standar role managed by the application
	 * @param role - role to be standared
	 */
	private String transform(String role) {
		// Role is normal
		if (role.equals("Normal")) {
			return "ROLE_NORMAL";
		}
		// Role is premium
		else if (role.equals("Premium")) {
			return "ROLE_PREMIUM";
		}
		// It is an unknown role
		else {
			return null;
		}
	}
}

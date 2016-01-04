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

@RestController
@RequestMapping("/login")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Value("${jwt.key}")
    	private String key;

	@Autowired
	protected UserRepository userRepository;


	public LoginController() {}

	public LoginController(UserRepositoryImpl userRepository){
        	this.userRepository = userRepository;
    	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error, HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		model.setViewName("loginPage");
		return model;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<User> login(@RequestParam("user") String id, @RequestParam("password") String password, 						  HttpServletRequest request, HttpServletResponse response) { 
		logger.info("Requested login with username " + id);
		//Verify the fields aren´t empty
		if (verifyFields(id, password)) {
			//There is a user with this username or email
			User user = userRepository.findByUsernameOrEmail(id);
			if (user != null) {
				BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
				// The password is correct
				if(encoder.matches(password, user.getPassword())) {
					logger.info("Login KEY: " + key);
					/*String token = AuthUtils.createToken(key, user.getUsername(), user.getAuthority(), 
							     new Date(System.currentTimeMillis() + 15*60*1000));
					logger.info("Tu JWT: " + token);
					logger.info("JWT: " + token.length());
					response.addCookie(new Cookie("Authorization", token));*/					
					// Put token in response
					return new ResponseEntity<>(user, HttpStatus.CREATED);
				}
				// The password is incorrect
				else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			//There aren't a user with this username or email
			else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}
		// There are empty fields
		else {
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

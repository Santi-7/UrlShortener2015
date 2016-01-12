package urlshortener2015.candypink.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import urlshortener2015.candypink.repository.UserRepository;
import urlshortener2015.candypink.repository.UserRepositoryImpl;

/**
 * This class manages the users of the database.
 * It can only be accessed with administrator permisions
 * It manages a get request to return all users of the database
 * and a post request to delete the desired user.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@RestController
@RequestMapping("/manageUsers")
public class ManageUsersController {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ManageUsersController.class);
	
	// Database
	@Autowired
	protected UserRepository userRepository;

	/** Default constructor of the controller */
	public ManageUsersController() {}

	/** Constructor with database "userRepository" */
	public ManageUsersController(UserRepositoryImpl userRepository) {
		this.userRepository = userRepository;
  	}
  
  	/**
  	 * It returns all users of the database
  	 */
  	@RequestMapping(method = RequestMethod.GET)
  	public ModelAndView getUsers() {
		logger.info("Requested all users info");
		ModelAndView model = new ModelAndView();
		// All users of the service
		model.addObject("users", userRepository.getAllUsers());
		// Redirection to manageUsersPage
		model.setViewName("manageUsersPage");
		return model;
  	}

	/**
	 * It deletes the user with username "user"
	 * and returns all the remaining users
	 */
	@RequestMapping(method = RequestMethod.POST)
  	public ModelAndView deleteUser(@RequestParam String user) {
		logger.info("Requested delete of username " + user);
		// Delete user
		userRepository.delete(user);
		// Load page again
		return getUsers();
  	}
}

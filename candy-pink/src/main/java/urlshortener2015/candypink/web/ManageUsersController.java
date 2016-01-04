package urlshortener2015.candypink.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener2015.candypink.repository.UserRepository;
import urlshortener2015.candypink.repository.UserRepositoryImpl;

@RestController
@RequestMapping("/admin/manageUsers")
public class ManageUsersController {

	private static final Logger logger = LoggerFactory.getLogger(ManageUsersController.class);
	
	@Autowired
	protected UserRepository userRepository;

	public ManageUsersController() {}

	public ManageUsersController(UserRepositoryImpl userRepository) {
		this.userRepository = userRepository;
  	}
  
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
}

package urlshortener2015.candypink.repository;

import java.util.List;

import urlshortener2015.candypink.domain.User;

/**
 * Interface that offers some methods to work with users of the application
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
public interface UserRepository {

	/**
	 * It returns a list of all the users managed in the database
	 * @returns a list of all the users managed in the database
	 */
	List<User> getAllUsers();

	/**
	 * It returns the user with username or email "id"
	 * @param id - username or email of the user to be returned
	 * @returns the user with username or email "id"
	 */
	User findByUsernameOrEmail(String id);

	/**
	 * It inserts the user "user" to the database and returns it
	 * if has been correctly inserted, null in another case.
	 * @param user - user to be inserted
	 * @returns - the user if has been correctly inserted, null in another case.
	 */
	User save(User user);

	/**
	 * It updates the values of the user "user" to its new details
	 * @param user - user to be updated
	 */
	void update(User user);

	/**
	 * Deletes the user with username "username"
	 * @param username - username of the user to be deleted
	 */
	void delete(String username);

	/**
	 * Deletes all the users of the database
	 */
	void deleteAll();

	/**
	 * Returns the number of users managed in the database
	 * @returns - the number of users managed in the database
	 */
	Long count();
}

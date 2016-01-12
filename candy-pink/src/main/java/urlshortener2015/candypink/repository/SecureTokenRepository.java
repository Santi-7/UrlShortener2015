package urlshortener2015.candypink.repository;

import java.util.List;
import urlshortener2015.candypink.domain.SecureToken;

/**
 * This interface define method to the access of the SecureToken data
 * in the database
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
public interface SecureTokenRepository {

	/**
	 * Search the secure token in the database. Return the token if it exist
	 * and null in other case.
	 * @param token - Token to search
	 * @return the token if it exist
	 * and null in other case.
	 */
	SecureToken findByToken(String token);

	/**
	 * Save the secure token in the database. Return the 
	 * @param token - Token to save
	 */
	SecureToken save(SecureToken token);

	/**
	 * Return true if the secure token is deleted, false in other case
	 * @param token - Secure token to delete
	 */
	boolean delete(String token);
}

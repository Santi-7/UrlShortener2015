package urlshortener2015.candypink.repository;

import java.util.List;

import urlshortener2015.candypink.domain.SecureToken;

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

	boolean delete(String token);
}

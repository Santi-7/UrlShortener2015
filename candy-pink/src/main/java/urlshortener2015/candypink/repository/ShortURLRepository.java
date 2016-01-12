package urlshortener2015.candypink.repository;

import java.util.List;

import urlshortener2015.candypink.domain.ShortURL;

/**
 * Interface that offers some methods to work with short Urls of the application
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
public interface ShortURLRepository {

	/**
	 * Returns the ShortUrl with hash "id", null in case that doesn't
	 * exists or an error has occurred
	 * @param id - hash of the ShortUrl to be returned
	 * @returns - the ShortUrl with hash "id", null in case that doesn't
	 * exists or an error has occurred
	 */
	ShortURL findByKey(String id);

	/**
	 * Returns a list of ShortUrls that redirect to "target" or
	 * an empty list if an error has occured
	 * @param target - Url where point the ShortUrls to be returned
	 * @return a list of ShortUrls that redirect to "target" or
	 * an empty list if an error has occured
	 */
	List<ShortURL> findByTarget(String target);

	/**
	 * It inserts the ShortUrl "su" to the database and returns it
	 * if has been correctly inserted, the proper ShortUrl if there
	 * is another SHortUrl with same hash, or null in another case.
	 * @param user - ShortUrl to be inserted
	 * @returns - the ShortUrl it if has been correctly inserted, the
	 * proper ShortUrl if there is another SHortUrl with same hash,
	 * or null in another case.
	 */
	ShortURL save(ShortURL su);

	/**
	 * It sets the "safe" value of the ShortUrl "su" to "safeness" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param su - ShortUrl to be updated
	 * @param safeness - value of the attribute safe of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	ShortURL mark(ShortURL urlSafe, boolean safeness);

	/**
	 * It sets the "spam" value of the ShortUrl "url" to "spam" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param url - ShortUrl to be updated
	 * @param spam - value of the attribute spam of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	ShortURL markSpam(ShortURL url, boolean spam);

	/**
	 * It sets the "reachable" value of the ShortUrl "url" to "reachable" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param url - ShortUrl to be updated
	 * @param reachable - value of the attribute reachable of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	ShortURL markReachable(ShortURL url, boolean reachable);

	/**
	 * It updates the values of the ShortUrl "su" to its new details
	 * @param su - ShortUrl to be updated
	 */
	void update(ShortURL su);

	/**
	 * Deletes the ShortUrl with hash "hash"
	 * @param hash - hash of the ShortUrl to be deleted
	 */
	void delete(String id);

	/**
	 * Returns the number of ShortUrls managed in the database
	 * @returns - the number of ShortUrls managed in the database
	 */
	Long count();

	/**
	 * Returns a list of the ShortUrls in the range [offset, limit] of the database,
	 * or an empty list if an error has occured
	 * @param limit - end point of the interval
	 * @offset - begin point of the interval
	 * @returns a list of the ShortUrls in the range [offset, limit] of the database
	 * or an empty list if an error has occured
	 */
	List<ShortURL> list(Long limit, Long offset);

	/**
	 * Returns a list of ShortUrls of the user "user" that exceed the value of response time
	 * of "maxResponseTime" and the shutdown time of "maxShutdownTime" and have a lower
	 * value of service time than "minServiceTime", or an empty list if an error has occured
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @param maxResponseTime - maximal value of the response time
	 * @param minServiceTime - minimal value of the service time
	 * @param maxShutdownTime - maximal value of the shutdown time
	 * @returns - a list of ShortUrls of the user "user" that exceed the value of response time
	 * of "maxResponseTime" and the shutdown time of "maxShutdownTime" and have a lower
	 * value of service time than "minServiceTime", or an empty list if an error has occured
	 */
	List<ShortURL> findByThresholdAndUser(String user,Integer maxResponseTime, Double minServiceTime,
										  Double maxShutdownTime);

	/**
	 * Returns a list of ShortUrls of the user with username "user"
	 * that have the value enabled "enabled", or an empty list
	 * if an error has occured
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @param enabled - value of the attribute enabled of the desired ShortUrls
	 * @returns - a list of ShortUrls of the user with username "user"
	 * that have the value enabled "enabled", or an empty list
	 * if an error has occured
	 */
	List<ShortURL> findByUserAndAvailability(String user,Boolean enabled);

	/**
	 * Returns a list of ShortUrls of the user with username "user", or
	 * an empty list if an error has occured
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @returns - a list of ShortUrls of the user with username "user", or
	 * an empty list if an error has occured
	 */
	List<ShortURL> findByUser(String user);
	
	/**
	 * Returns the a list of ShortUrls of the user with username "user"
	 * that have been realized the last 24 hours, or an empty list
	 * if an error has occured.
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @returns - a list of ShortUrls of the user with username "user"
	 * that have been realized the last 24 hours, or an empty list
	 * if an error has occured.
	 */
	List<ShortURL> findByUserlast24h(String user);

	/**
	 * It returns a list of ShortUrls that have been validated within the last
	 * "hours" hours, or an empty list if an error has occured.
	 * @param hours - number of the last hours to search the ShortUrls
	 * @returns - a list of ShortUrls that have been validated within the last
	 * "hours" hours, or an empty list if an error has occured.
	 */
	List<ShortURL> findByTimeHours(Integer hours);
}

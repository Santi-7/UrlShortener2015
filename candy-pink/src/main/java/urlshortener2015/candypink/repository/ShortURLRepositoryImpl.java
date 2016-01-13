package urlshortener2015.candypink.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import urlshortener2015.candypink.domain.ShortURL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * This class offers some methods to work with short Urls of the application
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {

	// Logger
	private static final Logger log = LoggerFactory
			.getLogger(ShortURLRepositoryImpl.class);

	// Used to fill the fields of the short Urls
	private static final RowMapper<ShortURL> rowMapper = new RowMapper<ShortURL>() {
		@Override
		public ShortURL mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new ShortURL(rs.getString("hash"), rs.getString("target"),
					null, rs.getString("token"), rs.getString("permission"),
					rs.getString("sponsor"), rs.getTimestamp("created"),
					rs.getString("owner"), rs.getInt("mode"), rs.getBoolean("safe"),
					rs.getInt("timeToBeSafe"),	rs.getBoolean("spam"), rs.getTimestamp("spamDate"),
					rs.getBoolean("reachable"), rs.getTimestamp("reachableDate"), rs.getString("ip"),
					rs.getString("country"), rs.getString("username"),rs.getInt("timesVerified"),
					rs.getInt("mediumResponseTime"), rs.getDouble("shutdownTime"),
					rs.getDouble("serviceTime"), rs.getBoolean("enabled"), rs.getInt("failsNumber"));
		}
	};

	// Adapter to the database
	@Autowired
	protected JdbcTemplate jdbc;

	/** Default constructor of the repository implementor */
	public ShortURLRepositoryImpl() {
	}

	/** 
	 * Constructor of the repository implementor with the database adapter "jdbc"
	 * @param jdbc - adapter to the database
	 */	
	public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Returns the ShortUrl with hash "id", null in case that doesn't
	 * exists or an error has occurred
	 * @param id - hash of the ShortUrl to be returned
	 * @returns - the ShortUrl with hash "id", null in case that doesn't
	 * exists or an error has occurred
	 */
	@Override
	public ShortURL findByKey(String id) {
		try {
			return jdbc.queryForObject("SELECT * FROM shorturl WHERE hash=?",
					rowMapper, id);
		} catch (Exception e) {
			log.debug("When select for key " + id, e);
			return null;
		}
	}

	/**
	 * Returns a list of ShortUrls that redirect to "target" or
	 * an empty list if an error has occured
	 * @param target - Url where point the ShortUrls to be returned
	 * @return a list of ShortUrls that redirect to "target" or
	 * an empty list if an error has occured
	 */
	@Override
	public List<ShortURL> findByTarget(String target) {
		try {
			return jdbc.query("SELECT * FROM shorturl WHERE target = ?",
					new Object[] { target }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for target " + target , e);
			return Collections.emptyList();
		}
	}

	/**
	 * It inserts the ShortUrl "su" to the database and returns it
	 * if has been correctly inserted, the proper ShortUrl if there
	 * is another SHortUrl with same hash, or null in another case.
	 * @param user - ShortUrl to be inserted
	 * @returns - the ShortUrl it if has been correctly inserted, the
	 * proper ShortUrl if there is another SHortUrl with same hash,
	 * or null in another case.
	 */
	@Override
	public ShortURL save(ShortURL su) {
		try {
			log.info("Inserting...");
			jdbc.update("INSERT INTO shorturl VALUES (?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					su.getHash(), su.getToken(), su.getUsers(), su.getTarget(), su.getSponsor(),
					su.getOwner(), su.getMode(), su.getSafe(),su.getTimeToBeSafe(), su.getSpam(), su.getSpamDate(),
					su.getReachable(), su.getReachableDate(), su.getIP(), su.getCountry(),
					su.getUsername(), su.getTimesVerified(), su.getMediumResponseTime(), su.getShutdownTime(),
					su.getServiceTime(),su.getEnabled(),su.getFailsNumber());
		// It already exists another ShortUrl with same username
		} catch (DuplicateKeyException e) {
			log.info("When insert for key " + su.getHash(), e);
			return su;
		} catch (Exception e) {
			log.info("When insert", e);
			return null;
		}
		log.info("INSERTED");
		return su;
	}

	/**
	 * It sets the "safe" value of the ShortUrl "su" to "safeness" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param su - ShortUrl to be updated
	 * @param safeness - value of the attribute safe of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	@Override
	public ShortURL mark(ShortURL su, boolean safeness) {
		try {
			jdbc.update("UPDATE shorturl SET safe=? WHERE hash=?", safeness,
					su.getHash());
			ShortURL res = new ShortURL();
			BeanUtils.copyProperties(su, res);
			new DirectFieldAccessor(res).setPropertyValue("safe", safeness);
			return res;
		} catch (Exception e) {
			log.debug("When update", e);
			return null;
		}
	}

	/**
	 * It sets the "spam" value of the ShortUrl "url" to "spam" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param url - ShortUrl to be updated
	 * @param spam - value of the attribute spam of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	@Override
	public ShortURL markSpam(ShortURL url, boolean spam) {
		try {
			jdbc.update("UPDATE shorturl SET spam=?, spamDate=CURRENT_TIMESTAMP WHERE hash=?", spam,
					url.getHash());
			return url;
		} catch (Exception e) {
			log.debug("When mark spam", e);
			return null;
		}	
	}
	
	/**
	 * It sets the "reachable" value of the ShortUrl "url" to "reachable" and returns
	 * the updated ShortUrl if exists, null in the case that doesn't exist or
	 * an error has occurred.
	 * @param url - ShortUrl to be updated
	 * @param reachable - value of the attribute reachable of the ShortUrl
	 * @returns - the updated ShortUrl or null in the case that doesn't exist or
	 * an error has occurred.
	 */
	@Override
	public ShortURL markReachable(ShortURL url, boolean reachable) {
		try {
			jdbc.update("UPDATE shorturl SET reachable=?, reachableDate=CURRENT_TIMESTAMP WHERE hash=?", reachable,
					url.getHash());
			return url;
		} catch (Exception e) {
			log.debug("When mark rachable", e);
			return null;
		}		
	}
	
	/**
	 * It updates the values of the ShortUrl "su" to its new details
	 * @param su - ShortUrl to be updated
	 */
	@Override
	public void update(ShortURL su) {
		try {
			jdbc.update(
					"update shorturl set target=?, sponsor=?, created=?, owner=?, mode=?, safe=?, timeToBeSafe=?, spam=?,"
							+" spamDate=?, reachable=?, reachableDate=?, ip=?, country=?, username=?, " +
							"timesVerified=?, mediumResponseTime=?, shutdownTime=?, serviceTime=?, enabled=?," +
							"failsNumber=? where hash=?",
					su.getTarget(), su.getSponsor(), su.getCreated(), su.getOwner(), su.getMode(), 
					su.getSafe(), su.getTimeToBeSafe(), su.getSpam(), su.getSpamDate(), su.getReachable(), su.getReachableDate(),
					su.getIP(), su.getCountry(), su.getUsername(),su.getTimesVerified(), su.getMediumResponseTime(),
					su.getShutdownTime(),su.getServiceTime(), su.getEnabled(), su.getFailsNumber(), su.getHash());
		} catch (Exception e) {
			log.debug("When update for hash " + su.getHash(), e);
		}
	}

	/**
	 * Deletes the ShortUrl with hash "hash"
	 * @param hash - hash of the ShortUrl to be deleted
	 */
	@Override
	public void delete(String hash) {
		try {
			jdbc.update("delete from shorturl where hash=?", hash);
		} catch (Exception e) {
			log.debug("When delete for hash " + hash, e);
		}
	}

	/**
	 * Returns the number of ShortUrls managed in the database
	 * @returns - the number of ShortUrls managed in the database
	 */
	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from shorturl",
					Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}

	/**
	 * Returns a list of the ShortUrls in the range [offset, limit] of the database,
	 * or an empty list if an error has occured
	 * @param limit - end point of the interval
	 * @offset - begin point of the interval
	 * @returns a list of the ShortUrls in the range [offset, limit] of the database
	 * or an empty list if an error has occured
	 */
	@Override
	public List<ShortURL> list(Long limit, Long offset) {
		try {
			return jdbc.query("SELECT * FROM shorturl LIMIT ? OFFSET ?",
					new Object[] { limit, offset }, rowMapper);
		} catch (Exception e) {
			log.debug("When select for limit " + limit + " and offset "
					+ offset, e);
			return Collections.emptyList();
		}
	}

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
	@Override
	public List<ShortURL> findByThresholdAndUser(String user,Integer maxResponseTime, Double minServiceTime,
												  Double maxShutdownTime) {
		try {
			return jdbc.query("SELECT * FROM SHORTURL WHERE username=? AND " +
							"(maxResponseTime>? OR minServiceTime<? OR maxShutdownTime>?)",
					new Object[]{user,maxResponseTime,minServiceTime,maxShutdownTime}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for shorturls of user: " +user+ " and thresholds", e);
			return Collections.emptyList();
		}
	}

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
	@Override
	public List<ShortURL> findByUserAndAvailability(String user,Boolean enabled) {
		try {
			return jdbc.query("SELECT * FROM SHORTURL WHERE username=? AND enabled=?",
					new Object[]{user,enabled}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for shorturls of user: " +user+ " and availability: "+ enabled, e);
			return Collections.emptyList();
		}
	}

	/**
	 * Returns a list of ShortUrls of the user with username "user", or
	 * an empty list if an error has occured
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @returns - a list of ShortUrls of the user with username "user", or
	 * an empty list if an error has occured
	 */
	@Override
	public List<ShortURL> findByUser(String user) {
		try {
			return jdbc.query("SELECT * FROM SHORTURL WHERE username=?",
					new Object[]{user}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for shorturls of user: " +user, e);
			return Collections.emptyList();
		}
	}

	/**
	 * Returns the a list of ShortUrls of the user with username "user"
	 * that have been realized the last 24 hours, or an empty list
	 * if an error has occured.
	 * @param user - username of the user whose ShortUrls are going to be returned
	 * @returns - a list of ShortUrls of the user with username "user"
	 * that have been realized the last 24 hours, or an empty list
	 * if an error has occured.
	 */
	@Override 
	public List<ShortURL> findByUserlast24h(String user) {
		try {
			return jdbc.query("SELECT * FROM SHORTURL WHERE username=? AND created>=(CURRENT_TIMESTAMP - interval '1' day) AND created<=CURRENT_TIMESTAMP",
					new Object[]{user}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for shorturls of user with time: " +user, e);
			return Collections.emptyList();
		}
	}

	/**
	 * It returns a list of ShortUrls that have been validated within the last
	 * "hours" hours, or an empty list if an error has occured.
	 * @param hours - number of the last hours to search the ShortUrls
	 * @returns - a list of ShortUrls that have been validated within the last
	 * "hours" hours, or an empty list if an error has occured.
	 */
	@Override
	public List<ShortURL> findByTimeHours(Integer hours) {
		try {
			return jdbc.query("SELECT * FROM SHORTURL WHERE spamDate<=(CURRENT_TIMESTAMP - interval '"+ hours.intValue()+"' hour)",
					new Object[]{hours}, rowMapper);
		} catch (Exception e) {
			log.debug("When select for shorturls with time: " +hours, e);
			return Collections.emptyList();
		}
	}
}

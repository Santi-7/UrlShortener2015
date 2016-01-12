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

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepository {

	private static final Logger log = LoggerFactory
			.getLogger(ShortURLRepositoryImpl.class);

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

	@Autowired
	protected JdbcTemplate jdbc;

	public ShortURLRepositoryImpl() {
	}

	public ShortURLRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

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

	@Override
	public ShortURL save(ShortURL su) {
		try {
			jdbc.update("INSERT INTO shorturl VALUES (?,?,?,?,?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					su.getHash(), su.getToken(), su.getUsers(), su.getTarget(), su.getSponsor(),
					su.getOwner(), su.getMode(), su.getSafe(),su.getTimeToBeSafe(), su.getSpam(), su.getSpamDate(),
					su.getReachable(), su.getReachableDate(), su.getIP(), su.getCountry(),
					su.getUsername(), su.getTimesVerified(), su.getMediumResponseTime(), su.getShutdownTime(),
					su.getServiceTime(),su.getEnabled(),su.getFailsNumber());
		} catch (DuplicateKeyException e) {
			log.debug("When insert for key " + su.getHash(), e);
			return su;
		} catch (Exception e) {
			log.debug("When insert", e);
			return null;
		}
		return su;
	}

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

	@Override
	public void delete(String hash) {
		try {
			jdbc.update("delete from shorturl where hash=?", hash);
		} catch (Exception e) {
			log.debug("When delete for hash " + hash, e);
		}
	}

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

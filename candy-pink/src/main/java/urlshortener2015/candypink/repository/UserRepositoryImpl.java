package urlshortener2015.candypink.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import urlshortener2015.candypink.domain.User;

/**
* This class offers some methods to work with users of the application
* @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
*/
@Repository
public class UserRepositoryImpl implements UserRepository {

	// Logger
	private static final Logger log = LoggerFactory
			.getLogger(UserRepositoryImpl.class);

	// Used to fill the fields of the users
	private static final RowMapper<User> rowMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getString("username"), rs.getString("password"),
					rs.getBoolean("enabled"), rs.getString("email"), rs.getString("authority"));
		}
	};

	// Adapter to the database
	@Autowired
	protected JdbcTemplate jdbc;

	/** Default constructor of the repository implementor */
	public UserRepositoryImpl() {
	}

	/** 
	 * Constructor of the repository implementor with the database adapter "jdbc"
	 * @param jdbc - adapter to the database
	 */
	public UserRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * It returns a list of all the users managed in the database
	 * @returns a list of all the users managed in the database
	 */
	@Override
	public List<User> getAllUsers() {
		try {
			return jdbc.query(
					 "SELECT u.username, u.password, u.enabled, u.email, a.authority"
				       + " FROM USERS u, AUTHORITIES a"
				       + " WHERE u.username=a.username", rowMapper);
		} catch (Exception e) {
			log.debug("When select for all users", e);
			return Collections.emptyList();
		}
	}

	/**
	 * It returns the user with username or email "id"
	 * @param id - username or email of the user to be returned
	 * @returns the user with username or email "id"
	 */
	@Override
	public User findByUsernameOrEmail(String id) {
		try {
			return jdbc.queryForObject("SELECT u.username, u.password, u.enabled, u.email, a.authority" 
			                            +" FROM USERS u, AUTHORITIES a "
			                            +"WHERE u.username=? OR u.email=? AND u.username=a.username",
         					    rowMapper, id, id);
		} catch (Exception e) {
			log.debug("When select for id " + id, e);
			return null;
		}
	}

	/**
	 * It inserts the user "user" to the database and returns it
	 * if has been correctly inserted, null in another case.
	 * @param user - user to be inserted
	 * @returns - the user if has been correctly inserted, null in another case.
	 */
	@Override
	public User save(User user) {
		try {
			log.info("Password " + user.getPassword());
			jdbc.update("INSERT INTO USERS VALUES (?, ?, ?, ?)",
					user.getUsername(), user.getPassword(), user.getEnabled(), user.getEmail());
			jdbc.update("INSERT INTO AUTHORITIES VALUES (?, ?)",
					user.getUsername(), user.getAuthority());
			return user;
		// It already exists another user with same username
		} catch (DuplicateKeyException e) {
			log.info("When insert for user with user " + user.getUsername());
			return null;
		} catch (Exception e) {
			log.info("When insert a user " + e);
			return null;
		}
	}
	
	/**
	 * It updates the values of the user "user" to its new details
	 * @param user - user to be updated
	 */
	@Override
	public void update(User user) {
		log.info("Username: "+user.getUsername());
		try {
			jdbc.update("update Users set password=?, email=? WHERE username=?",
				     user.getPassword(), user.getEmail(), user.getUsername());
			jdbc.update("update Authorities set authority=? where userame=?",
				     user.getAuthority(), user.getUsername());
		} catch (Exception e) {
			log.info("When update for user " + user.getUsername(), e);
		}
	}

	/**
	 * Deletes the user with username "username"
	 * @param username - username of the user to be deleted
	 */
	@Override
	public void delete(String username) {
		try {
			jdbc.update("delete from Authorities where username=?", username);
			jdbc.update("delete from Users where username=?", username);
		} catch (Exception e) {
			log.debug("When delete for username " + username, e);
		}
	}

	/**
	 * Deletes all the users of the database
	 */
	@Override
	public void deleteAll() {
		try {
			jdbc.update("delete from Users");
			jdbc.update("delete from Authorities");
		} catch (Exception e) {
			log.debug("When delete all", e);
		}
	}

	/**
	 * Returns the number of users managed in the database
	 * @returns - the number of users managed in the database
	 */
	@Override
	public Long count() {
		try {
			return jdbc.queryForObject("select count(*) from Users", Long.class);
		} catch (Exception e) {
			log.debug("When counting", e);
		}
		return -1L;
	}
}

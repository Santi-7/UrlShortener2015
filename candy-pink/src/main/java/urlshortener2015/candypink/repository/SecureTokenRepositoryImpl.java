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

import urlshortener2015.candypink.domain.SecureToken;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class implements methods to the access of the SecureToken data
 * in the database
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
@Repository
public class SecureTokenRepositoryImpl implements SecureTokenRepository {

	// logger
	private static final Logger log = LoggerFactory
			.getLogger(SecureTokenRepositoryImpl.class);

	// For fill data
	private static final RowMapper<SecureToken> rowMapper = new RowMapper<SecureToken>() {
		@Override
		public SecureToken mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SecureToken(rs.getString("token"));
		}
	};

	// Access to database
	@Autowired
	protected JdbcTemplate jdbc;

	/**
	 * Constructor without paramas
	 */
	public SecureTokenRepositoryImpl() {
	}

	/**
	 * Constructor with database connection
	 * @param jdbc - Connection to database
	 */
	public SecureTokenRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/**
	 * Search the secure token in the database. Return the token if it exist
	 * and null in other case.
	 * @param token - Token to search
	 * @return the token if it exist
	 * and null in other case.
	 */
	@Override
	public SecureToken findByToken(String token) {
		try {
			return jdbc.queryForObject("SELECT * FROM SECURETOKEN WHERE TOKEN=?", rowMapper, token);		
		} catch (Exception e) {
			log.debug("When select for token ", e);
			return null;
		}
	}

	/**
	 * Save the secure token in the database. Return the 
	 * @param token - Token to save
	 */
	@Override
	public SecureToken save(SecureToken token) {
		try {
			log.info("Token " + token.getToken());
			jdbc.update("INSERT INTO SECURETOKEN VALUES (?)",
					token.getToken());
			return token;
		} catch (DuplicateKeyException e) {
			log.info("When insert a token " + token.getToken());
			return null;
		} catch (Exception e) {
			log.info("When insert a token " + e);
			return null;
		}
	}

	/**
	 * Return true if the secure token is deleted, false in other case
	 * @param token - Secure token to delete
	 */
	@Override
	public boolean delete(String token) {
		try {
			jdbc.update("delete from SECURETOKEN where TOKEN=?", token);
			return true;
		} catch (Exception e) {
			log.debug("When delete for token " + token, e);
			return false;
		}
	}
}

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

@Repository
public class SecureTokenRepositoryImpl implements SecureTokenRepository {

	private static final Logger log = LoggerFactory
			.getLogger(SecureTokenRepositoryImpl.class);


	private static final RowMapper<SecureToken> rowMapper = new RowMapper<SecureToken>() {
		@Override
		public SecureToken mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SecureToken(rs.getString("token"));
		}
	};

	@Autowired
	protected JdbcTemplate jdbc;

	public SecureTokenRepositoryImpl() {
	}

	public SecureTokenRepositoryImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}


	private List<SecureToken> list() {
		try {
			return jdbc.query("SELECT * FROM SECURETOKEN", rowMapper);		
		} catch (Exception e) {
			log.debug("When select for token ", e);
			return null;
		}
	}
	
	@Override
	public SecureToken findByToken(String token) {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		for(SecureToken s : list()) {
			if(encoder.matches(token,s.getToken())) {
				return s;
			}
		}
		return null;
	}

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

	@Override
	public void delete(String token) {
		try {
			jdbc.update("delete from SECURETOKEN where TOKEN=?", token);
		} catch (Exception e) {
			log.debug("When delete for token " + token, e);
		}
	}
}

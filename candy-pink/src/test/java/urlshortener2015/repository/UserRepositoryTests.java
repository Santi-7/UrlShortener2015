package urlshortener2015.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;
import static urlshortener2015.repository.fixture.UserFixture.user1;
import static urlshortener2015.repository.fixture.UserFixture.user1Modified;
import static urlshortener2015.repository.fixture.UserFixture.user2;
import static urlshortener2015.repository.fixture.UserFixture.userPassword;
import static urlshortener2015.repository.fixture.UserFixture.userEmail;
import static urlshortener2015.repository.fixture.UserFixture.userAuthority;
import static urlshortener2015.repository.fixture.UserFixture.badUser;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import urlshortener2015.candypink.domain.User;
import urlshortener2015.candypink.repository.UserRepository;
import urlshortener2015.candypink.repository.UserRepositoryImpl;

public class UserRepositoryTests {

	private EmbeddedDatabase db;
	private UserRepository repository;
	private JdbcTemplate jdbc;

	@Before
	public void setup() {
		db = new EmbeddedDatabaseBuilder().setType(HSQL)
				.addScript("schema-hsqldb.sql").build();
		jdbc = new JdbcTemplate(db);
		repository = new UserRepositoryImpl(jdbc);
	}

	@Test
	public void thatSavePersistsTheUser() {
		assertNotNull(repository.save(user1()));
		assertSame(jdbc.queryForObject("select count(*) from USERS",
				Integer.class), 1);
	}

	@Test
	public void thatSavePassword() {
		assertNotNull(repository.save(userPassword()));
		assertSame(jdbc.queryForObject("select password from USERS",
				String.class), userPassword().getPassword());
	}
	
	@Test
	public void thatSaveEmail() {
		assertNotNull(repository.save(userEmail()));
		assertSame(jdbc.queryForObject("select email from USERS",
				String.class), userEmail().getEmail());
	}

	@Test
	public void thatIsEnabled() {
		assertNotNull(repository.save(user1()));
		assertSame(jdbc.queryForObject("select enabled from USERS",
				Boolean.class), user1().getEnabled());
	}

	@Test
	public void thatSaveAuthority() {
		assertNotNull(repository.save(userAuthority()));
		User u = repository.findByUsernameOrEmail(userAuthority().getUsername());
		assertSame(u.getAuthority(), userAuthority().getAuthority());
	}

	@Test
	public void thatSaveADuplicateUserIsSafelyIgnored() {
		repository.save(user1());
		assertNull(repository.save(user1()));
		assertSame(jdbc.queryForObject("select count(*) from USERS",
				Integer.class), 1);
	}

	@Test
	public void thatErrorsInSaveReturnsNull() {
		assertNull(repository.save(badUser()));
		assertSame(jdbc.queryForObject("select count(*) from USERS",
				Integer.class), 0);
	}

	@Test
	public void thatFindByUsernameOrEmailReturnsAUser() {
		repository.save(userEmail());
		// Test find with an username
		User u = repository.findByUsernameOrEmail(userEmail().getUsername());
		assertNotNull(u);
		assertSame(u.getUsername(),userEmail().getUsername());
	}

	@Test
	public void thatFindByKeyReturnsNullWhenFails() {
		repository.save(user1());
		assertNull(repository.findByUsernameOrEmail(user2().getUsername()));
	}
	
	@Test
	public void thatDeleteDelete() {
		repository.save(user1());
		repository.save(user2());
		repository.delete(user1().getUsername());
		assertEquals(repository.count().intValue(), 1);
		repository.delete(user2().getUsername());
		assertEquals(repository.count().intValue(), 0);
	}

	@Test
	public void thatUpdateUpdate() {
		repository.save(user1());
		User u = repository.findByUsernameOrEmail(user1().getUsername());
		assertEquals(u.getPassword(), "pwd1");
		repository.update(user1Modified());
		u = repository.findByUsernameOrEmail(user1Modified().getUsername());
		assertEquals(u.getPassword(), "pwd2");
	}
	
	@Test
	public void thatGetAllUsers() {
		assertEquals(repository.getAllUsers().size(), 0);
		repository.save(user1());
		assertEquals(repository.getAllUsers().size(), 1);
		repository.save(user2());
		assertEquals(repository.getAllUsers().size(), 2);
	}
	
	@After
	public void shutdown() {
		db.shutdown();
	}

}

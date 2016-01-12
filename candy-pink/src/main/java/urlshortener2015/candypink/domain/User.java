package urlshortener2015.candypink.domain;

/**
 * This class represents a user of the application
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
public class User {
    
    	/* Username of the user */
	private String username;
	/* Password of the user */
	private String password;
	/* Activated user. In order to pass Spring Security */
	private Boolean enabled;
	/* Email of the user */
	private String email;
	/* Role of the user (Normal, Premium, Admin) */
	private String authority;

	/**
	 * Cretes a new user with the parameters passed
	 * @param username - username of the user
	 * @param password - password of the user
	 * @param enabled - if user is activated
	 * @param email - email of the user
	 * @param authority - role of the user
	 * @returns the user created
	 */
	public User(String username, String password, Boolean enabled, String email, String authority) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.email = email;
		this.authority = authority;
	}
	
	/**
	 * Returns the username of the user
	 * @returns the username of the user
	 */
	public String getUsername() {
		return this.username;	
	}
	
	/**
	 * It sets the username of the user to "username"
	 * @param username - new username of the user
	 */
	public void setUsername(String username) {
		this.username = username;	
	}
	
	/**
	 * Returns the password of the user
	 * @returns the password of the user
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * It sets the password of the user to "password"
	 * @param password - new password of the user
	 */
	public void setPassword(String password) {
		this.password = password;	
	}
	
	/**
	 * Returns true if the user is enabled
	 * @returns true if the user is enabled
	 */
	public Boolean getEnabled() {
		return this.enabled;
	}
	
	/**
	 * It sets the enabled value of the user to "enabled"
	 * @param enabled - new enabled value of the user
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;	
	}

	/**
	 * Returns the email of the user
	 * @returns the email of the user
	 */
	public String getEmail() {
		return this.email;	
	}
	
	/**
	 * It sets the email of the user to "email"
	 * @param email - new email of the user
	 */
	public void setEmail(String email) {
		this.email = email;	
	}

	/**
	 * Returns the authority of the user
	 * @returns the authority of the user
	 */
	public String getAuthority() {
		return this.authority;
	}
	
	/**
	 * It sets the authority of the user to "authority"
	 * @param authority - new authority of the user
	 */
	public void setAuthority(String authority) {
		this.authority = authority;	
	}
}

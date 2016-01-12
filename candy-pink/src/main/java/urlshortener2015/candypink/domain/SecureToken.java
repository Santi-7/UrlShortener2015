package urlshortener2015.candypink.domain;

/**
 * Secure token used to garantee the secure access 
 * to a URI
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
public class SecureToken {
    
	// Secure token
	private String token;

	/**
	 * Creates a secure token
	 * @param token - secure token
	 */
	public SecureToken(String token) {
		this.token = token;
	}

	/**
	 * Return the secure token
	 * @return the secure token
	 */
	public String getToken() {
		return this.token;	
	}
}

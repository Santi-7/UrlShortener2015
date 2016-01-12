package urlshortener2015.candypink.auth.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class offer methods to process uris to filter
 * and create Json Web Tokens.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */
public class AuthUtils {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

	// End of line in the system
	private static final String newLine = System.getProperty("line.separator");
	// Uris that must be checked
	private static final String URIS = "/admin:GET-Admin,POST-Admin,PUT-Admin,DELETE-Admin:end:"+ newLine
					 + "/link:POST-Normal:end:"+ newLine
					 + "/profile:GET-Normal,POST-Normal,PUT-Normal,DELETE-Normal:end:"+ newLine
					 + "/login:GET-Not,POST-Not,PUT-Not,DELETE-Not:end:"+ newLine;

	// Uris that must be filtered
	private static final String FILTER = "/login,/link,/profile,/admin";

	/**
	 * Returns a JWT for a client with a time of expiration and encrypted with a key.
	 * @param username - Name of the user 
	 * @param role - Role of the user
	 * @param key - Key to the creation
	 * @param expiration - Time of expiration
	 * @return A JWT for a user
	 */
	public static String createToken(String username, String role, String key, Date expiration) {
		return Jwts.builder().setSubject(username)
            	.claim("role", role).setIssuedAt(new Date()).setExpiration(expiration)
            	.signWith(SignatureAlgorithm.HS256, key).compact();
	 }

	/**
	 * Returns a list with the uris to filter
	 * @return A list with the uris to filter
	 */ 
	public static ArrayList<String> filterList() {
		ArrayList<String> uris = new ArrayList<String>();
		Scanner scan = new Scanner(FILTER);
		scan.useDelimiter(",");
		// There are more uris
		while(scan.hasNext()) {
			// Add the uri
			uris.add(scan.next());
		}
		return uris;
	}
	
	/**
	 * Return a list of uris with the permission required for each method
	 * @return A list of uris with the permission required for each method
	 */
	public static AuthURI[] buildAuthURIs() {
		ArrayList<AuthURI> authList = new ArrayList<AuthURI>();
		Scanner scan = new Scanner(URIS);
		scan.useDelimiter(":|,|-");
		// There are more uris
		while(scan.hasNextLine()) {
			String uri = scan.next();
			logger.info("URI: " + uri);
			HashMap<String, String> methods = new HashMap<String, String>();
			// There are more methods
			while(!scan.hasNext("end")) {
				String method = scan.next();
				logger.info("METHOD: " + method);
				String permission = scan.next();
				logger.info("PERMISSION: " + permission);
				methods.put(method, permission);		
			}
			// Add de uri
			authList.add(new AuthURI(uri, methods));
			scan.nextLine();
		}
		AuthURI[] auth = new AuthURI[authList.size()];
		auth = authList.toArray(auth);
		logger.info("TAM: " + auth.length);
		return auth;
	}
}

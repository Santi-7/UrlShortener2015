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
	 * 
	 * @param - username
	 * @param - role
	 * @param - key
	 * @param expiration
	 * /
	public static String createToken(String username, String role, String key, Date expiration) {
		return Jwts.builder().setSubject(username)
            	.claim("role", role).setIssuedAt(new Date()).setExpiration(expiration)
            	.signWith(SignatureAlgorithm.HS256, key).compact();
	 }

	public static ArrayList<String> filterList() {
		ArrayList<String> uris = new ArrayList<String>();
		Scanner scan = new Scanner(FILTER);
		scan.useDelimiter(",");
		while(scan.hasNext()) {
			uris.add(scan.next());
			logger.info("URI PILLADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");	
		}
		return uris;
	}
	public static AuthURI[] buildAuthURIs() {
		ArrayList<AuthURI> authList = new ArrayList<AuthURI>();
		Scanner scan = new Scanner(URIS);
		scan.useDelimiter(":|,|-");
		while(scan.hasNextLine()) {
			String uri = scan.next();
			logger.info("URI: " + uri);
			HashMap<String, String> methods = new HashMap<String, String>();
			while(!scan.hasNext("end")) {
				String method = scan.next();
				logger.info("METHOD: " + method);
				String permission = scan.next();
				logger.info("PERMISSION: " + permission);
				methods.put(method, permission);		
			}
			authList.add(new AuthURI(uri, methods));
			scan.nextLine();
		}
		AuthURI[] auth = new AuthURI[authList.size()];
		auth = authList.toArray(auth);
		logger.info("TAM: " + auth.length);
		return auth;
	}
}

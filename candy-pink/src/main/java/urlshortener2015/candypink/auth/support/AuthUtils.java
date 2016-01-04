package urlshortener2015.candypink.auth.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class AuthUtils {

	private static final Logger logger = LoggerFactory.getLogger(AuthUtils.class);

	private static final String newLine = System.getProperty("line.separator");
	private static final String URIS = "/admin:GET-Admin,POST-Admin,PUT-Admin,DELETE-Admin:end:"+ newLine
					 + "/link:POST-Normal:end:"+ newLine;

	public static String createToken(String username, String role, String key, Date expiration) {
		return Jwts.builder().setSubject(username)
            	.claim("role", role).setIssuedAt(new Date()).setExpiration(expiration)
            	.signWith(SignatureAlgorithm.HS256, key).compact();
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

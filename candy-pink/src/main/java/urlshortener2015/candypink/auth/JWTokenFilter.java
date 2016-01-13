package urlshortener2015.candypink.auth;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.filter.GenericFilterBean;
import urlshortener2015.candypink.auth.support.AuthURI;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import urlshortener2015.candypink.auth.support.AuthUtils;

/**
 * This class is a JWT filter to filter the uris and check the permissions
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 *  We thank Bangladesh Green Team [1] and Niels Dommerholt [2] due to we have used their code to do our JWTokenFiler.java
 * [1]-(https://github.com/teruyi/UrlShortener2015/blob/master/bangladesh-green/src/main/java/urlshortener/bangladeshgreen/auth/WebTokenFilter.java)
 * [2]-(https://github.com/nielsutrecht/jwt-angular-spring/tree/master/src/main/java/com/nibado/example/jwtangspr)
 */
@Configurable
public class JWTokenFilter extends GenericFilterBean {
   
    // logger
    private static final Logger log = LoggerFactory.getLogger(JWTokenFilter.class);

    // key
    private String key;

    // list of uris
    private AuthURI[] uris;
    
    /**
     * Create a JWT filter with a list of uris with their permissions and the
     * key that must be used.
     * @param key - Key used
     * @param uris - List of uris 
     */
    public JWTokenFilter(String key, AuthURI[] uris){
        this.key = key;
        this.uris = uris;
    }

    /**
     * Filter the uri of the request
     */
    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
		// Obtain servlets
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		String jwtoken = AuthUtils.getToken(request);
		// Obtain the required permission
		String permission = requiredPermission(request.getRequestURI(), request.getMethod());
		// All users can access
		if (permission == null) {
			log.info("Authentication not required");
			chain.doFilter(req, res);
		}
		// Only not authenticated users can access
		else if (permission.equals("Not")) {
			// Error
			log.info("Requires not authenticate");
			try {
				// Obtain the jwt
				if (jwtoken != null) {
					final Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtoken).getBody();
					log.info("NAMEUSER: " + claims.getSubject());
				}
			}
			// The token is expired
			catch (ExpiredJwtException expiredException) {
				// Token Expired
				jwtoken = null;
			}
			// The user is authenticated
			if (jwtoken != null) {
				log.info("Authenticated yet");
				forbidden(response);
			}
			// The user is not authenticated
			else {
				log.info("Correct, no authenticated");
				chain.doFilter(req, res);
			}
		}
		// Only authenticated users can access
		else {
			// No authenticated
			if (jwtoken == null) {
				log.info("No authenticate");
				forbidden(response);
			}
			// Authenticated
			else {
				log.info("Authenticated-Go parse!");
				try {
					//Parse claims from JWT
					final Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtoken).getBody();
					log.info("NAMEUSER: " + claims.getSubject());
					String role = claims.get("role", String.class);
					log.info("Role: " + role);
					log.info("Parsed");
					// Has not permission
					if (permission.equals("Admin") && !role.equals("ROLE_ADMIN") ||
							permission.equals("Premium") && role.equals("ROLE_NORMAL")) {
						log.info("Not Permission");
						forbidden(response);
					}
					// Has permission
					else {
						log.info("Yes Permission");
						request.setAttribute("claims", claims);
						chain.doFilter(req, res);
					}
				}
				// The token has expired
				catch (ExpiredJwtException expiredException) {
					log.info("Expired");
					forbidden(response);
				}
				// The format is incorrect
				catch (final SignatureException | NullPointerException | MalformedJwtException e) {
					e.printStackTrace();
					log.info("Format incorrect");
					forbidden(response);
				}
			}
		}
	}

	/**
	 * Redirect the user to the forbidden page
	 * @param response - Response of the server
	 */
	private void forbidden(HttpServletResponse response) throws IOException{
		response.setStatus(403);
		response.sendRedirect("403.html");	
	}
	
	/**
	 * Returns the permission required to access a uri with 
	 * a method
	 * @param uri - The uri to access
	 * @param method - The method for the access
	 * @return the permission required to accesa a uri with
	 * a method
	 */
	private String requiredPermission(String uri, String method) {
		log.info("URI: " + uri);
		log.info("METHOD: " + method);
		for(int i = 0; i < uris.length; i++) {
			if(uri.contains(uris[i].getUri())) {
				return uris[i].getPermission(method);
			}
		}
		return null;		
	}
}

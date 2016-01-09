package urlshortener2015.candypink.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.filter.GenericFilterBean;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.Cookie;
import io.jsonwebtoken.*;

import urlshortener2015.candypink.auth.support.AuthURI;
import urlshortener2015.candypink.auth.support.AuthUtils;

/**
 *  We thank Bangladesh Green Team [1] and Niels Dommerholt [2] due to we have used their code to do our JWTokenFiler.java
 * [1]-(https://github.com/teruyi/UrlShortener2015/blob/master/bangladesh-green/src/main/java/urlshortener/bangladeshgreen/auth/WebTokenFilter.java)
 * [2]-(https://github.com/nielsutrecht/jwt-angular-spring/tree/master/src/main/java/com/nibado/example/jwtangspr)
 */
@Configurable
public class JWTokenFilter extends GenericFilterBean {
   
    private static final Logger log = LoggerFactory.getLogger(JWTokenFilter.class);

    private String key;

    private AuthURI[] uris;
    
    public JWTokenFilter(String key, AuthURI[] uris){
        this.key = key;
        this.uris = uris;
    }

    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
	// Obtain servlets
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response  = (HttpServletResponse) res;
	String jwtoken = null;
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
	for (int i = 0; i < cookies.length; i++) {
		if(cookies[i].getName().equals("Authorization")) {
			jwtoken = cookies[i].getValue();
		}	
	}
		log.info("KEY: " + key);
		log.info("JWT: " + jwtoken);
		String permission = requiredPermission(request.getRequestURI(), request.getMethod());
		// All users
		if(permission == null) {
			log.info("Authentication not required");
			chain.doFilter(req, res); 
		}
		else if(permission.equals("Not")) {
			//Error
			log.info("Requires not authenticate");
			if(jwtoken != null) {
				log.info("Authenticated yet");
				forbidden(response);
			}		
			else {
				log.info("Correct, no authenticated");
				chain.doFilter(req, res); 
			}
		}
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
					if(permission.equals("Admin") && !role.equals("ROLE_ADMIN") ||
					   permission.equals("Premium") && role.equals("ROLE_NORMAL")) {
						log.info("Not PErmission");
						forbidden(response);
						//Error
					}
					// Has permission
					else {
						log.info("Yes Permission");
						request.setAttribute("claims",claims);	
                        			chain.doFilter(req, res);
					}
                }
                catch(ExpiredJwtException expiredException){
                    // Token Expired
					log.info("Expired");
					forbidden(response);
                }
                catch (final SignatureException  | NullPointerException  |MalformedJwtException e) {
                    // Format incorrect
					e.printStackTrace();
					log.info("Format incorrect");
					forbidden(response);
                }
			}
		}
	}
	}

	private void forbidden(HttpServletResponse response) throws IOException{
		response.setStatus(403);
		response.sendRedirect("403.html");	
	}
	private String requiredPermission(String uri, String method) {
		log.info("URI PEDIDA: " + uri);
		log.info("METHOD: " + method);
		for(int i = 0; i < uris.length; i++) {
			if(uri.contains(uris[i].getUri())) {
				//log.info("PREMIO: " + uris[i].getUri());
				log.info("PERMIO: " + uris[i].getPermission(method));	
				return uris[i].getPermission(method);
			}
		}
		return null;		
	}
}

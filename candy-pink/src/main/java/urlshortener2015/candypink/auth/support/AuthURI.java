package urlshortener2015.candypink.auth.support;

import java.util.HashMap;

/**
 * This class represent a uri and the permissions required to reach it,
 * for each method(GET, POST, DELETE, PUT)
 * @author - A.Ãlvarez, I.GascÃ³n, S.Gil, D.Nicuesa 
 */
public class AuthURI {
    
    // URI
    private String uri;
    // Methods with permissions
    private HashMap<String, String> methods;
  
    /**
     * Creates a new object AuthURi with a uri and the permission
     * required tu access
     * @param methods - Methods and permissions required
     * @param uri - Uri  
     */
    public AuthURI(String uri, HashMap<String, String> methods) {
	this.uri = uri;
	this.methods = methods;

    }

    /**
     * Returns the uri
     * @returns uri 
     */
    public String getUri() {
	return this.uri;
    }
	
    /**
     * Assign the permission [permission] to the method [method]
     * @param method -
     * @param permission -
     */
    public void setMethod(String method, String permission) {
	methods.put(method, permission);
    }
	
    /**
     * Returns the permission of the method [method]
     * @returns permission of the method [method]
     */
    public String getPermission(String method) {
        return methods.get(method);
    }
}

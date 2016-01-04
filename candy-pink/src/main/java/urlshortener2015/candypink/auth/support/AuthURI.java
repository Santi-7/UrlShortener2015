package urlshortener2015.candypink.auth.support;

import java.util.HashMap;

public class AuthURI {

    private String uri;
    private HashMap<String, String> methods;

    public AuthURI(String uri, HashMap<String, String> methods) {
	this.uri = uri;
	this.methods = methods;

    }

    public String getUri() {
	return this.uri;
    }
	
    public void setMethod(String method, String permission) {
	methods.put(method, permission);
    }
	
    public String getPermission(String method) {
        return methods.get(method);
    }
}


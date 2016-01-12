package urlshortener2015.candypink.uploader;

/**
 * This class contains the information that it's sent
 * from the client to the server with the information
 * of the html form in which the client writes the
 * URLs to short.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */ 
public class HtmlFormInfo {
	
	private String subs;
	private String url;
	private String token;
	
	/**
	 * Simple constructor for the websocket controller
	 * to construct a JSON document when it receives
	 * a HtmlFormInfo object.
	 */ 
	public HtmlFormInfo(){}
	
	/**
	 * Class constructor.
	 * @param s - Queue on which the client is subscribed.
	 * @param u - URL to short.
	 * @param t - Token of the users session.
	 */ 
	public HtmlFormInfo(String s, String u, String t){
		subs=s;
		url=u;
		token=t;
	}
	
	//Getters
	
	public String getSubs(){
		return subs;
	}
	
	public String getUrl(){
		return url;
	}
	
	public String getToken(){
		return token;
	}
}

package urlshortener2015.candypink.uploader;

import org.springframework.web.multipart.MultipartFile;

/**
 * Contains all the information needed by the server to
 * short the URLs sent by the client. This object will
 * be put on the queue.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */ 
public class QueueObject {
	
	private Object toShort;
	private String uri;
	private String type;
	private String username;
	private String role;
	
	/**
	 * Class constructor.
	 * @param o - This will be either a MultipartFile or a String
	 * @param t - "sv" if the object is a file or "url" if it is a String
	 * @param u - The user's username.
	 * @param r - The user's role.
	 */ 
	public QueueObject(Object o, String t, String u, String r){
		toShort = o;
		type=t;
		username=u;
		role=r;
	}
	
	//Setters and getters
	
	public void setUri(String u){
		uri=u;
	}
	
	public Object getToShort(){
		return toShort;
	}
	
	public String getUri(){
		return uri;
	}
	
	public String getUsername(){
		return username;
	}	
	
	public String getRole(){
		return role;
	}
	
	public String getType(){
		return type;
	}
	
}

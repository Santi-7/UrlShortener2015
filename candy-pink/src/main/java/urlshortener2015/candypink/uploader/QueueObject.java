package urlshortener2015.candypink.uploader;

import org.springframework.web.multipart.MultipartFile;

public class QueueObject {
	
	private Object toShort;
	private String uri;
	private String type;
	private String username;
	private String role;
	
	public QueueObject(Object o, String t, String u, String r){
		toShort = o;
		type=t;
		username=u;
		role=r;
	}
	
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

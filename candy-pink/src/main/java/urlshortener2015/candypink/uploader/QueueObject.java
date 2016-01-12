package urlshortener2015.candypink.uploader;

import org.springframework.web.multipart.MultipartFile;

public class QueueObject {
	
	private MultipartFile file;
	private String user;
	private String type;
	
	public QueueObject(MultipartFile f, String t){
		file = f;
		type=t;
	}
	
	public void setUser(String u){
		user=u;
	}
	
	public MultipartFile getFile(){
		return file;
	}
	
	public String getUser(){
		return user;
	}	
	
}

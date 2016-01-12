package urlshortener2015.candypink.uploader;


public class UpdateMessage{
	
	private String status;
	private String user;
	
	public UpdateMessage (String s, String u){
		status = s;
		user = u;
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getUser(){
		return user;
	}
}

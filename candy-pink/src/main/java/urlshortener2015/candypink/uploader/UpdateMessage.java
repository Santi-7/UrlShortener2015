package urlshortener2015.candypink.uploader;


/**
 * Contains the information that will be sent to the client 
 * after shorting an URL.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa 
 */ 
public class UpdateMessage{
	
	private String status;
	private String user;
	
	/**
	 * Class constructor.
	 * @param s - Either a success or an error message with the URL to short
	 * and the shortened URL if succeded.
	 * @param u - Uri on which the client is subscribed.
	 */ 
	public UpdateMessage (String s, String u){
		status = s;
		user = u;
	}
	
	//Getters
	
	public String getStatus(){
		return status;
	}
	
	public String getUser(){
		return user;
	}
}

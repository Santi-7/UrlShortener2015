package urlshortener2015.candypink.uploader;

/**
 * Contains the information about the queue uri
 * on which the client will have to subscribe.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */ 
public class QueueInfo {
	
	private String uri;
	
	/**
	 * Class constructor.
	 * @param u - Queue uri on which the client will have to subscribe.
	 */ 
	public QueueInfo (String u){
		uri=u;
	}
	
	public String getUri(){
		return uri;
	}
}

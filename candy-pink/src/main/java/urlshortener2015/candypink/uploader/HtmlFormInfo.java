package urlshortener2015.candypink.uploader;

public class HtmlFormInfo {
	
	private String subs;
	private String url;
	private String token;
	
	public HtmlFormInfo(){}
	
	public HtmlFormInfo(String s, String u, String t){
		subs=s;
		url=u;
		token=t;
	}
	
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

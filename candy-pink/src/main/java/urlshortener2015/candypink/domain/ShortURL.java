package urlshortener2015.candypink.domain;

import java.net.URI;
import java.sql.Date;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private String token;
	private String users;
	private String sponsor;
	private Date created;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private Boolean spam;
	private String spamDate;
	private Boolean reachable;
	private String reachableDate;
	private String ip;
	private String country;
	private String username;

	public ShortURL(String hash, String target, URI uri, String token, String users,
			String sponsor, Date created, String owner, Integer mode,
			Boolean safe, Boolean spam, String spamDate, Boolean reachable,
			String reachableDate, String ip, String country, String username) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.token = token;
		this.users = users;
		this.sponsor = sponsor;
		this.created = created;
		this.owner = owner;
		this.mode = mode;
		this.safe = safe;
		this.spam = spam;
		this.spamDate = spamDate;
		this.reachable = reachable;
		this.reachableDate = reachableDate; 
		this.ip = ip;
		this.country = country;
		this.username = username;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public String getToken() {
		return token;
	}
	
	public String getUsers() {
		return users;
	}
	
	public Date getCreated() {
		return created;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getMode() {
		return mode;
	}

	public String getSponsor() {
		return sponsor;
	}

	public Boolean getSafe() {
		return safe;
	}

	public Boolean getSpam() {
		return spam;
	}

	public String getSpamDate() {
		return spamDate;
	}

	public Boolean getReachable() {
		return reachable;	
	}

	public String getReachableDate() {
		return reachableDate;	
	}

	public String getIP() {
		return ip;
	}

	public String getCountry() {
		return country;
	}

	public String getUsername() {
		return username;
	}

	public void setSpamDate(String date){this.spamDate = date;}

	public void setReachableDate(String date){this.spamDate = date;}

	public void setReachable(Boolean reach){this.reachable = reach;}

	public void setSpam(Boolean spam){this.spam = spam;}
}

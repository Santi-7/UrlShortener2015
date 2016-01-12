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
	private Integer timeToBeSafe;
	private Boolean spam;
	private Date spamDate;
	private Boolean reachable;
	private Date reachableDate;
	private String ip;
	private String country;
	private String username;
	private Integer timesVerified;
	private Integer mediumResponseTime;
	private Integer shutdownTime;
	private Integer serviceTime;

	public ShortURL(String hash, String target, URI uri, String token,
					String users, String sponsor, Date created, String owner,
					Integer mode, Boolean safe,Integer timeToBeSafe, Boolean spam, Date spamDate,
					Boolean reachable, Date reachableDate, String ip, String country,
					String username, Integer timesVerified, Integer mediumResponseTime,
					Integer shutdownTime, Integer serviceTime) {
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
		this.timeToBeSafe = timeToBeSafe;
		this.spam = spam;
		this.spamDate = spamDate;
		this.reachable = reachable;
		this.reachableDate = reachableDate;
		this.ip = ip;
		this.country = country;
		this.username = username;
		this.timesVerified = timesVerified;
		this.mediumResponseTime = mediumResponseTime;
		this.shutdownTime = shutdownTime;
		this.serviceTime = serviceTime;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	public Boolean getSafe() {
		return safe;
	}

	public void setSafe(Boolean safe) {
		this.safe = safe;
	}

	public Boolean getSpam() {
		return spam;
	}

	public void setSpam(Boolean spam) {
		this.spam = spam;
	}

	public Date getSpamDate() {
		return spamDate;
	}

	public void setSpamDate(Date spamDate) {
		this.spamDate = spamDate;
	}

	public Boolean getReachable() {
		return reachable;
	}

	public void setReachable(Boolean reachable) {
		this.reachable = reachable;
	}

	public Date getReachableDate() {
		return reachableDate;
	}

	public void setReachableDate(Date reachableDate) {
		this.reachableDate = reachableDate;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public Integer getTimeToBeSafe() {
		return timeToBeSafe;
	}

	public void setTimeToBeSafe(Integer timeToBeSafe) {
		this.timeToBeSafe = timeToBeSafe;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getTimesVerified() {
		return timesVerified;
	}

	public void setTimesVerified(Integer timesVerified) {
		this.timesVerified = timesVerified;
	}

	public Integer getMediumResponseTime() {
		return mediumResponseTime;
	}

	public void setMediumResponseTime(Integer mediumResponseTime) {
		this.mediumResponseTime = mediumResponseTime;
	}

	public Integer getShutdownTime() {
		return shutdownTime;
	}

	public void setShutdownTime(Integer shutdownTime) {
		this.shutdownTime = shutdownTime;
	}

	public Integer getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Integer serviceTime) {
		this.serviceTime = serviceTime;
	}
}
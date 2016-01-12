package urlshortener2015.candypink.domain;

import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;

public class ShortURL {

	private String hash;
	private String target;
	private URI uri;
	private String token;
	private String users;
	private String sponsor;
	private Timestamp created;
	private String owner;
	private Integer mode;
	private Boolean safe;
	private Integer timeToBeSafe;
	private Boolean spam;
	private Timestamp spamDate;
	private Boolean reachable;
	private Timestamp reachableDate;
	private String ip;
	private String country;
	private String username;
	private Integer timesVerified;
	private Integer mediumResponseTime;
	private Double shutdownTime;
	private Double serviceTime;
	private Boolean enabled;
	private Integer failsNumber;

	public ShortURL(){}

	public ShortURL(String hash, String target, URI uri, String token, String users, String sponsor,
					Timestamp created, String owner, Integer mode, Boolean safe, Integer timeToBeSafe,
					Boolean spam, Timestamp spamDate, Boolean reachable, Timestamp reachableDate, String ip,
					String country, String username, Integer timesVerified, Integer mediumResponseTime,
					Double shutdownTime, Double serviceTime, Boolean enabled, Integer failsNumber) {
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
		this.enabled = enabled;
		this.failsNumber = failsNumber;
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

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
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

	public Integer getTimeToBeSafe() {
		return timeToBeSafe;
	}

	public void setTimeToBeSafe(Integer timeToBeSafe) {
		this.timeToBeSafe = timeToBeSafe;
	}

	public Boolean getSpam() {
		return spam;
	}

	public void setSpam(Boolean spam) {
		this.spam = spam;
	}

	public Timestamp getSpamDate() {
		return spamDate;
	}

	public void setSpamDate(Timestamp spamDate) {
		this.spamDate = spamDate;
	}

	public Boolean getReachable() {
		return reachable;
	}

	public void setReachable(Boolean reachable) {
		this.reachable = reachable;
	}

	public Timestamp getReachableDate() {
		return reachableDate;
	}

	public void setReachableDate(Timestamp reachableDate) {
		this.reachableDate = reachableDate;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
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

	public Double getShutdownTime() {
		return shutdownTime;
	}

	public void setShutdownTime(Double shutdownTime) {
		this.shutdownTime = shutdownTime;
	}

	public Double getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(Double serviceTime) {
		this.serviceTime = serviceTime;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getFailsNumber() {
		return failsNumber;
	}

	public void setFailsNumber(Integer failsNumber) {
		this.failsNumber = failsNumber;
	}
}
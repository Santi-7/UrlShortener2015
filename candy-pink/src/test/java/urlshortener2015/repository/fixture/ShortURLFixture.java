package urlshortener2015.repository.fixture;

import urlshortener2015.candypink.domain.ShortURL;

import java.net.URI;
import java.sql.Timestamp;

public class ShortURLFixture {

	public static ShortURL url1() {
		return new ShortURL("1", "http://www.unizar.es/", null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL url1modified() {
		return new ShortURL("1", "http://www.unizar.org/", null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL url2() {
		return new ShortURL("2", "http://www.unizar.es/", null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL url3() {
		return new ShortURL("3", "http://www.google.es/", null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL badUrl() {
		return new ShortURL(null, null, null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL urlSponsor() {
		return new ShortURL("3", null, null, null,
				null, "Sponsor", null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL urlSafe() {
		return new ShortURL("4", null, null, null,
				null, "Sponsor", null, null, 0, true,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL urlSpam() {
		return new ShortURL("4", null, null, null,
				null, null, null, null, 0, false,0, true,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}
	
	public static ShortURL urlReachable() {
		return new ShortURL("4", null, null, null,
				null, null, null, null, 0, false,0, false,
				null, true, null, null, null, null,0,0,0.0,0.0,true,0);
	}

	public static ShortURL url1user1() {
		return new ShortURL("10", null, null, null,
				null, "Sponsor", null, null, 0, false,0, false,
				null, true, null, null, null, "user1",0,0,0.0,0.0,true,0);
	}
	public static ShortURL url2user1() {
		return new ShortURL("11", null, null, null,
				null, "Sponsor", null, null, 0, false,0, false,
				null, true, null, null, null, "user1",0,0,0.0,0.0,true,0);
	}

	public static ShortURL url3user1Created() {
		return new ShortURL("13", null, null, null,
				null, "Sponsor", null, null, 0, false,0, false,
				null, true, null, null, null, "user1",0,0,0.0,0.0,true,0);
	}
	
	public static ShortURL url4user1Created() {
		return new ShortURL("14", null, null, null,
				null, "Sponsor", null, null, 0, false,0, false,
				null, true, null, null, null, "user1",0,0,0.0,0.0,true,0);
	}
}

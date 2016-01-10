package urlshortener2015.candypink.domain;

/**
 * Created by david on 9/01/16.
 */
public class FishyURL {

    private String targetURL;
    private String dateSince;
    private String message;

    public String getTargetURL() {
        return targetURL;
    }

    public String getDateSince() {
        return dateSince;
    }

    public String getErrorMsg() {
        return message;
    }

    public FishyURL(String targetURL, String dateSince, String message) {
        this.targetURL = targetURL;
        this.dateSince = dateSince;
        this.message = message;
    }
}

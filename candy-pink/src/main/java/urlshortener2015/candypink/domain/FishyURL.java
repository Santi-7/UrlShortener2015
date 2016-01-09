package urlshortener2015.candypink.domain;

/**
 * Created by david on 9/01/16.
 */
public class FishyURL {

    private String targetURL;
    private String dateSince;
    private String errorMsg;

    public String getTargetURL() {
        return targetURL;
    }

    public String getDateSince() {
        return dateSince;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public FishyURL(String targetURL, String dateSince, String errorMsg) {
        this.targetURL = targetURL;
        this.dateSince = dateSince;
        this.errorMsg = errorMsg;
    }
}

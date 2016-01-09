package urlshortener2015.candypink.exceptions;

import java.util.Date;

public class ResourceNotFoundException extends RuntimeException {

    private Date htmlBody;
    
    public ResourceNotFoundException(Date htmlBody){
        //super(...)
        this.htmlBody = htmlBody;
    }
    
    public Date getDate() {
        return htmlBody;
    }
}

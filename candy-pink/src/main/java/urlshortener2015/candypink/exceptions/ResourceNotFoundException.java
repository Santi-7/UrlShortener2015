package urlshortener2015.candypink.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private String htmlBody;
    
    public ResourceNotFoundException(String htmlBody){
        //super(...)
        this.htmlBody = htmlBody;
    }
    
    public String getDate() {
        return htmlBody;
    }
}

package urlshortener2015.candypink.checker.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Adapter to external service. It uses the Google Safe
 * Browsing service to check if the uri is spam|malware.
 * In addition, checks the availability of the url and its
 * response time.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@Component
public class AdapterToExternImpl implements AdapterToExtern {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${client}")
    private String client;

    @Value("${appver}")
    private String appver;

    @Value("${pver}")
    private String pver;

    /**
     * Checks the url is safe and available.
     * @param url - URL that must be analyzed
     * @return - Map containing the results from the verification
     */
    @Override
    public Map<String, Object> checkUrl(String url) {
        Client checkerClient = ClientBuilder.newClient();
        Map<String,Object> results = new HashMap<String,Object>();
        //Preparing URI to check
        WebTarget target = checkerClient.target("https://sb-ssl.google.com/safebrowsing/api/lookup");
        WebTarget targetWithQueryParams = target.queryParam("key", apiKey);
        targetWithQueryParams = targetWithQueryParams.queryParam("client", client);
        targetWithQueryParams = targetWithQueryParams.queryParam("appver",appver);
        targetWithQueryParams = targetWithQueryParams.queryParam("pver",pver);
        targetWithQueryParams = targetWithQueryParams.queryParam("url",url);

        //Request to check if URI is malware
        Response response = targetWithQueryParams.request(MediaType.TEXT_PLAIN_TYPE).get();
        Boolean spam = false,reach = false;
        if(response.getStatus()==204){//Uri is safe
            spam = false;

        }else if(response.getStatus()==200){//Uri is unsafe
            spam = true;
        }

        //Request to check if URI is reachable
        try {
            target = checkerClient.target(url);
            Long beforeRequest = System.currentTimeMillis();
            response = target.request(MediaType.TEXT_PLAIN_TYPE).get();
            Integer responseTime = new Long(System.currentTimeMillis() - beforeRequest).intValue();
            if(response.getStatus() == 200){
                reach = true;
            }
            results.put("responseTime",responseTime);
        }catch(ProcessingException e){}
        results.put("Spam", spam);
        results.put("Reachable", reach);
        return results;
    }
}

package urlshortener2015.candypink.checker.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class provides an asynchronous method to consume
 * the queue of urls
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@Component
public class QueueConsumerBean {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    //Max mediumresponsetime(ms) tolerable
    @Value("${maxResponseTime}")
    private Integer maxResponseTime;

    //Min service(percentage) times tolerable
    @Value("${minServiceTime}")
    private String minServiceTime;

    //Max times(percentage) the uri can be down
    @Value("${maxDownTime}")
    private String maxDownTime;

    //Max times before disable uri
    @Value("${maxTimesBeforeDisable}")
    private Integer timesBeforeDisable;

    //Max times before delete uri
    @Value("${maxTimesBeforeDelete}")
    private Integer timesBeforeDelete;

    //Queue shared between processes
    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    //Adapter to external service
    @Autowired
    protected AdapterToExtern adapter;

    //Repo of urls shortened
    @Autowired
    protected ShortURLRepository shortURLRepository;


    /*
    * This method verifies asynchronously the state of the url
    * using the adapter. It updates stats like if it is reachable,
    * spam and its times.
    * */
    @Async
    public void extractAndCheck() {
        LOG.info("La cola esta esperando a que haya algo metido");
        LOG.info("Estos son los parametros:" + maxResponseTime + ", " + minServiceTime + ", " + maxDownTime);
        while (true) {
            try {
                String url = sharedQueue.take();
                //This checks uri
                Map<String, Object> map = adapter.checkUrl(url);
                //Now the uri has been checked
                //Takes it from repo
                ShortURL shortURL = shortURLRepository.findByTarget(url).get(0);
                LOG.info(shortURL.getTarget() + " expires " + shortURL.getTimeToBeSafe());
                Timestamp now = new Timestamp(System.currentTimeMillis());//Date now
                //Checks if it is reachable
                if (shortURL.getReachableDate() == null || shortURL.getReachable() || (Boolean) map.get("Reachable")) {
                    //Url is reachable and date must be updated
                    shortURL.setReachable((Boolean) map.get("Reachable"));
                    shortURL.setReachableDate(now);
                    //Calculate times of the threshold
                    //Medium response time
                    Integer mediumResponseTime = 0;
		    if(map.get("responseTime") != null) {		    
			mediumResponseTime = calculateMediumResponseTime((Integer) map.get("responseTime"),
                            shortURL.getMediumResponseTime(), shortURL.getTimesVerified());
		    }
                    shortURL.setMediumResponseTime(mediumResponseTime);
                    //Service time
                    Double sTime = shortURL.getServiceTime();
                    sTime = (sTime * shortURL.getTimesVerified() + 1) / (shortURL.getTimesVerified() + 1);
                    //Shutdown time
                    Double downTime = shortURL.getShutdownTime();
                    downTime = (downTime * shortURL.getTimesVerified() + 1) / (shortURL.getTimesVerified() + 1);
                    shortURL.setShutdownTime(downTime);
                    shortURL.setServiceTime(sTime);
                }
                //Checks if security has expired
                if (shortURL.getSafe()) {
                    Timestamp expires = calculateExpiresTime(shortURL.getCreated(), shortURL.getTimeToBeSafe());
                    shortURL.setSafe(!expires.before(now));
                }
                //Has been verified once more
                shortURL.setTimesVerified(shortURL.getTimesVerified() + 1);

                evaluate(shortURL);
                //Verify spam
                shortURL.setSpam((Boolean) map.get("Spam"));
                shortURL.setSpamDate(now);
                //Updates the db
                shortURLRepository.update(shortURL);
            } catch (InterruptedException e) {
            }
        }
    }

    /*
    * Calculates the new medium response time
    * */
    private int calculateMediumResponseTime(int timeToCount, int timeStored, int times){
        return ((timeStored*times) + timeToCount)/(times + 1);
    }

    /**
     * Calculates the time the URL is going to expire
     */
    private Timestamp calculateExpiresTime(Timestamp created, Integer timeToBeSafe){
        Timestamp updated = new Timestamp(created.getTime());
        LOG.info("Creada: " + updated.getTime());
        //timeToBeSafe is given in days, so needs to be converted to hours
        updated.setTime(updated.getTime()+(timeToBeSafe*86400*1000));
        LOG.info("Ahora: " + new Timestamp(System.currentTimeMillis()).getTime());
        LOG.info("Expira a esta fecha: " + updated.getTime());
       return updated;
    }

    /**
     * Verifies if url has reached threshold values or not.
     * Disables the url if must so
     */
    private void evaluate(ShortURL url){
        Double doubleMaxDownTime =  Double.parseDouble(maxDownTime);
        Double doubleMinServiceTime =  Double.parseDouble(minServiceTime);
        if(url.getMediumResponseTime() > maxResponseTime || url.getShutdownTime() > doubleMaxDownTime
                || url.getServiceTime() < doubleMinServiceTime){
            //There are problems with the url and threshold has been reached
            Integer failsNumber = url.getFailsNumber();
            url.setFailsNumber(failsNumber+1);
        }else{
            //No problems, we reset the fails
            url.setFailsNumber(0);
        }
        if(url.getEnabled() && (url.getFailsNumber() >= timesBeforeDisable)){
            //Url must be disabled due to it has reached the time  before disable
            url.setEnabled(false);
        }
    }
}

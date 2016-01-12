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
import java.sql.Date;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by david on 1/01/16.
 */
@Component
public class QueueConsumerBean {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    //Max mediumresponsetime(secs) tolerable
    @Value("${maxResponseTime}")
    private Integer maxResponseTime;

    //Min service(hours) time tolerable
    @Value("${minServiceTime}")
    private Integer minServiceTime;

    //Max time(hours) the uri can be down
    @Value("${maxDownTime}")
    private Integer maxDownTime;

    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    @Autowired
    protected AdapterToExtern adapter;

    @Autowired
    protected ShortURLRepository shortURLRepository;


    /*
    * This method verifies asynchronously the state of the url
    * using the adapter. It updates stats like if it is reachable,
    * spam and its times.
    * */
    @Async
    public void extractAndCheck(){
        LOG.info("La cola esta esperando a que haya algo metido");
        while(true){
        try {
            String url = sharedQueue.take();
            LOG.info("Hay "+shortURLRepository.count()+ " urls en la bd");
            LOG.info("Se ha metido algo en la cola");
            LOG.info("Comprobando " + url);
            //This checks uri
            Map<String,Object> map = adapter.checkUrl(url);
            //Now the uri has been checked
            ShortURL shortURL = shortURLRepository.findByTarget(url).get(0);
            LOG.info(shortURL.getTarget() +" expires " + shortURL.getTimeToBeSafe());
            Date now = new Date(System.currentTimeMillis());
            //Checks if it is reachable
            if(shortURL.getReachableDate()== null || shortURL.getReachable() || (Boolean)map.get("Reachable")){
                shortURL.setReachable((Boolean)map.get("Reachable"));
                shortURL.setReachableDate(now);
                Integer mediumResponseTime = calculateMediumResponseTime((Integer)map.get("responseTime"),
                        shortURL.getMediumResponseTime(),shortURL.getTimesVerified());
                shortURL.setMediumResponseTime(mediumResponseTime);
            }
            //Checks if security has expired
            if(shortURL.getSafe()){
                Date expires = calculateExpiresTime(shortURL.getCreated(),shortURL.getTimeToBeSafe());
                shortURL.setSafe(!expires.before(now));
            }
            shortURL.setSpam((Boolean)map.get("Spam"));
            shortURL.setSpamDate(now);
            shortURLRepository.update(shortURL);
            LOG.info("La url es spam: " + map.get("Spam"));
            LOG.info("La url es alcanzable: " + map.get("Reachable"));
         } catch (InterruptedException e) {}

        }
    }

    public int calculateMediumResponseTime(int timeToCount, int timeStored, int times){
        return ((timeStored*times) + timeToCount)/(times + 1);
    }

    public Date calculateExpiresTime(Date created, Integer timeToBeSafe){
        Date updated = new Date(created.getTime());
        //timeToBeSafe is given in days, so needs to be converted to hours
        updated.setTime(updated.getTime()+(timeToBeSafe*86400*1000));
        LOG.info("Expira a esta fecha:");
       return updated;
    }
}

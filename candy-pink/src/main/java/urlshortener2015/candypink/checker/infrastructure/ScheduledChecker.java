package urlshortener2015.candypink.checker.infraestructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by david on 2/01/16.
 */
@Component
public class ScheduledChecker {

    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    @Autowired
    protected ShortURLRepository shortURLRepository;

    /*
    * This method is executed every hour after the previous
    * excution
    * */
    @Scheduled(fixedDelay = 360000)
    private void checkingDaemon(){
        List<ShortURL> resultList = shortURLRepository.findByTimeHours(new Integer(1));
        for(ShortURL url : resultList){
            try {
                sharedQueue.put(url.getTarget());   //This is blocking
            } catch (InterruptedException e) {}

        }
    }
}

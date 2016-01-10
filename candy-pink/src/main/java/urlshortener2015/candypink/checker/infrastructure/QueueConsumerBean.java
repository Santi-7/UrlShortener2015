package urlshortener2015.candypink.checker.infraestructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by david on 1/01/16.
 */
@Component
public class QueueConsumerBean {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    @Autowired
    protected AdapterToExtern adapter;

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Async
    public void extractAndCheck(){
        LOG.info("La cola esta esperando a que haya algo metido");
        while(true){
        try {
            String url = sharedQueue.take();
            LOG.info("Hay "+shortURLRepository.count()+ " urls en la bd");
            LOG.info("Se ha metido algo en la cola");
            LOG.info("Comprobando " + url);
            Map<String,Boolean> map = adapter.checkUrl(url);
            ShortURL shortURL = shortURLRepository.findByTarget(url).get(0);
            if(shortURL.getReachableDate()== null || shortURL.getReachable() || map.get("Reachable")){
                shortURL.setReachable(map.get("Reachable"));
                shortURL.setReachableDate(new Date(System.currentTimeMillis()));
            }
            shortURL.setSpam(map.get("Spam"));
            shortURL.setSpamDate(new Date(System.currentTimeMillis()));
            shortURLRepository.update(shortURL);
            LOG.info("La url es spam: " + map.get("Spam"));
            LOG.info("La url es alcanzable: " + map.get("Reachable"));
         } catch (InterruptedException e) {}

        }
    }
}

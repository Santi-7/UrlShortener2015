package checker.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.repository.ShortURLRepository;

import javax.annotation.Resource;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
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
            LOG.info("Se ha metido algo en la cola");
            LOG.info("Comprobando " + url);
            Map<String,Boolean> map = adapter.checkUrl(url);
            ShortURL shortURL = shortURLRepository.findByTarget(url).get(0);
            if(shortURL.getReachable() || map.get("Reachable")){
                shortURL.setReachableDate(new Date().toString());
            }
            shortURL.setReachable(map.get("Reachable"));
            shortURL.setSpam(map.get("Spam"));
            shortURL.setSpamDate(new Date().toString());

            shortURLRepository.update(shortURL);
            LOG.info("La url es spam: " + map.get("Spam"));
            LOG.info("La url es alcanzable: " + map.get("Reachable"));
         } catch (InterruptedException e) {}

        }
    }
}

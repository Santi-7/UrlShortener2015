package urlshortener2015.candypink.checker.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.domain.User;
import urlshortener2015.candypink.repository.ShortURLRepository;
import urlshortener2015.candypink.repository.UserRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;


/**
 * Created by david on 2/01/16.
 */
@Component
public class ScheduledChecker {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    //Max mediumresponsetime(ms) tolerable
    @Value("${maxResponseTime}")
    private Integer maxResponseTime;

    //Min service(hours) time tolerable
    @Value("${minServiceTime}")
    private String minServiceTime;

    //Max time(hours) the uri can be down
    @Value("${maxDownTime}")
    private String maxDownTime;

    //Max times before disable uri
    @Value("${maxTimesBeforeDisable}")
    private Integer timesBeforeDisable;

    //Max times before delete uri
    @Value("${maxTimesBeforeDelete}")
    private Integer timesBeforeDelete;

    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JavaMailSender javaMailSender;

    /*
    * This method is executed every hour after the previous
    * excution
    * */
    @Scheduled(fixedDelay = 360000)
    private void checkingDaemon(){
        LOG.info("checkingDaemon is active");
        List<ShortURL> resultList = shortURLRepository.findByTimeHours(new Integer(1));
        for(ShortURL url : resultList){
            try {
                sharedQueue.put(url.getTarget());   //This is blocking
            } catch (InterruptedException e) {}

        }
    }

    /*
   * This method is executed every day after the previous
   * excution. Sends email to the users in order to notify
   * the state of the urls with problems
   * */
    @Scheduled(fixedDelay = 86400000)
    private void emailDaemon() {
        LOG.info("emailDaemon is active");
        List<User> userList = userRepository.getAllUsers();
        for(User user : userList) {
            List<ShortURL> thresholdList = shortURLRepository.findByThresholdAndUser(user.getUsername(),
                   maxResponseTime, Double.parseDouble(minServiceTime), Double.parseDouble(maxDownTime));

            List<ShortURL> disabledList = shortURLRepository.findByUserAndAvailability(user.getUsername(),false);


            List<ShortURL> urlsToDelete = new ArrayList<ShortURL>();
            for(ShortURL url : disabledList){
                //Urls to delete
                if(url.getFailsNumber()>=timesBeforeDelete){
                    urlsToDelete.add(url);
                }
            }
            /*This removes urls to delete*/
            boolean success = disabledList.removeAll(urlsToDelete);

            /*
            * SEND EMAIL WITH THRESHOLDS HERE
            * */
            /*
            * SEND EMAIL WITH DISABLED HERE
            * */
            /*
            * SEND EMAIL WITH DELETED HERE
            * */
            for(ShortURL urlToDelete : urlsToDelete){
                //Urls are deleted
                shortURLRepository.delete(urlToDelete.getHash());
            }
        }
    }
}

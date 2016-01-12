package urlshortener2015.candypink.checker.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import urlshortener2015.candypink.domain.ShortURL;
import urlshortener2015.candypink.domain.User;
import urlshortener2015.candypink.repository.ShortURLRepository;
import urlshortener2015.candypink.repository.UserRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * This class schedules the check of the urls using an adapter
 * to the external service. In addition, it sends e-mails to
 * user notifying about bad-state urls.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
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
    private String maxDownTime;;

    //Max times before delete uri
    @Value("${maxTimesBeforeDelete}")
    private Integer timesBeforeDelete;

    //Queue shared to pass the url to check
    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    //Repo of url shortened
    @Autowired
    protected ShortURLRepository shortURLRepository;

    //Repo of users
    @Autowired
    protected UserRepository userRepository;

    //Java email sender
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
        //For each user
        for(User user : userList) {
            //Get threshold list
            List<ShortURL> thresholdList = shortURLRepository.findByThresholdAndUser(user.getUsername(),
                    maxResponseTime, Double.parseDouble(minServiceTime), Double.parseDouble(maxDownTime));
            //Get urls disabled
            List<ShortURL> disabledList = shortURLRepository.findByUserAndAvailability(user.getUsername(), false);
            //Get urls that must be deleted
            List<ShortURL> urlsToDelete = new ArrayList<ShortURL>();
            for (ShortURL url : disabledList) {
                //Urls to delete
                if (url.getFailsNumber() >= timesBeforeDelete) {
                    urlsToDelete.add(url);
                }
            }
            /*This removes urls to delete from disabled list*/
            disabledList.removeAll(urlsToDelete);
            checkUrlLists(user, thresholdList, disabledList, urlsToDelete);
        }
    }


    /*
    * This method checks the lists of urls shortened and sends an email to
     * the user.
    * */
    private void checkUrlLists(User user,List<ShortURL> thresholdList,List<ShortURL> disabledList,
                                List<ShortURL> urlsToDelete){
        if (!disabledList.isEmpty() || !disabledList.isEmpty()) {
            String messageString = "Estimado usuario " + user.getUsername() + " :\n" +
                    "Se han detectado irregularidades en las urls almacenadas.\n";
            if(!thresholdList.isEmpty()) {
                messageString += "Las urls que presentan tiempos inapropiados son:\n";
                for (ShortURL url : thresholdList) {
                    messageString += "\t->" + url.getTarget() + "\n";
                }
            }
            if (!disabledList.isEmpty()) {
                messageString += "Las urls que se han deshabilitado son:\n";
                for (ShortURL url : disabledList) {
                    messageString += "\t->" + url.getTarget() + "\n";
                }
            }
            if (!urlsToDelete.isEmpty()) {
                messageString += "Las urls que se van a borrar son:\n";
                for (ShortURL url : urlsToDelete) {
                    messageString += "\t->" + url.getTarget() + "\n";
                    //Urls are deleted
                    shortURLRepository.delete(url.getHash());
                }
            }
            sendMailToUser(user.getEmail(),messageString);
        }
    }


    /*
    * Sends an email to the user indicated by param asynchronously
    * @param user
    * */
    @Async
    private void sendMailToUser(String mail,String messageContent){
        LOG.info("Enviando mensaje");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("candyshort.noreply@gmail.com");
        message.setTo(mail);
        message.setSubject("Information about your candyshort account");
        message.setText(messageContent);
        javaMailSender.send(message);
        LOG.info("Enviado");
    }
}

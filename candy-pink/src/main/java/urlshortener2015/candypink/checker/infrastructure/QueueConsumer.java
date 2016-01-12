package urlshortener2015.candypink.checker.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Class used to initialize the asynchronous consumer of
 * the url queue.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */

@Component
public class QueueConsumer {

    @Autowired
    protected QueueConsumerBean consumerBean;

    /*
    * Executed after the instantiation of the class.
    * Starts the asynchronous process consuming the queue
    * */
    @PostConstruct
    public void startCheckDaemon(){
        consumerBean.extractAndCheck();
    }


}

package urlshortener2015.candypink.checker.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by david on 1/01/16.
 */

@Component
public class QueueConsumer {

    @Autowired
    protected QueueConsumerBean consumerBean;

    @PostConstruct
    public void startCheckDaemon(){
        consumerBean.extractAndCheck();
    }


}

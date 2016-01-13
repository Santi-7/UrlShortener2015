package urlshortener2015.candypink.checker.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class provides a service for the endpoint to
 * use.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */

@Service
public class CheckerServiceImpl implements CheckerService {

    @Resource
    protected LinkedBlockingQueue<String> sharedQueue;

    @Override
    public boolean queueUrl(String url){
       return sharedQueue.offer(url);
    }
}

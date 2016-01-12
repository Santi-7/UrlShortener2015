package urlshortener2015.candypink.checker.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by david on 1/01/16.
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

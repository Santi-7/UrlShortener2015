package urlshortener2015.candypink.uploader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Class used to initialize the asynchronous consumer of
 * the CSVs and URls queue.
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */
@Component
public class CsvQueueConsumer {
	
	@Autowired
	private CsvQueueConsumerBean consumerBean;
	
	/**
    * Executed after the instantiation of the class.
    * Starts the asynchronous process consuming the queue.
    */
	@PostConstruct
	public void startConsumer(){
		consumerBean.extractAndProcess();
	}
	
}

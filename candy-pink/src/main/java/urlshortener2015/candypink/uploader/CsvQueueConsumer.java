package urlshortener2015.candypink.uploader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CsvQueueConsumer {
	
	@Autowired
	private CsvQueueConsumerBean consumerBean;
	
	@PostConstruct
	public void startConsumer(){
		consumerBean.extractAndProcess();
	}
	
}

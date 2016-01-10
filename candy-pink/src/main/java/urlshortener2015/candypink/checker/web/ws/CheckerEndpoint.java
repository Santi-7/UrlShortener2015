package urlshortener2015.candypink.checker.service.web.ws;

import urlshortener2015.candypink.checker.service.CheckerService;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerRequest;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CheckerEndpoint {

	@Autowired
	protected CheckerService checker;

	@PayloadRoot(namespace = "http://checker/web/ws/schema", localPart = "getCheckerRequest")
	@ResponsePayload
	public GetCheckerResponse translator(@RequestPayload GetCheckerRequest request) {
		GetCheckerResponse response = new GetCheckerResponse();
		boolean result = checker.queueUrl(request.getUrl());
		String code;
		if(result){
			code = "ok";
		}else{
			code = "error";
		}
		response.setResultCode(code);
		return response;
	}

}

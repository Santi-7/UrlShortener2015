package urlshortener2015.candypink.checker.web.ws;

import urlshortener2015.candypink.checker.service.CheckerService;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerRequest;
import urlshortener2015.candypink.checker.web.ws.schema.GetCheckerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Endpoint through which clients can access to the service that
 * checks the urls and their access times
 * @author - A.Alvarez, I.Gascon, S.Gil, D.Nicuesa
 */

@Endpoint
public class CheckerEndpoint {

	@Autowired
	protected CheckerService checker;

	/**
	 * Receives a request in SOAP format containing the url that has to be
	 * analyzed, it communicates the web service the request.
	 * @param request - SOAP message containing the url
	 * @return - SOAP response with a response code. [ok] if everything has gone well,
	 * [error] otherwise
	 */
	@PayloadRoot(namespace = "http://urlshortener2015/candypink/checker/web/ws/schema", localPart = "getCheckerRequest")
	@ResponsePayload
	public GetCheckerResponse checkingMethod(@RequestPayload GetCheckerRequest request) {
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

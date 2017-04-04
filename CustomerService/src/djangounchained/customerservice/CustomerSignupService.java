package djangounchained.customerservice;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import djangounchained.util.DynamoDbHandler;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

public class CustomerSignupService {
	private RequestClass request = null;
	private DynamoDbHandler dynamo = null;
	
	public CustomerSignupService(RequestClass request){
		this.request = request;
		this.dynamo = new DynamoDbHandler();
	}
	public ResponseClass signup(){
		PutItemOutcome outcome = dynamo.createCustomer(request);
		ResponseClass response = new ResponseClass();
		if(outcome == null){
			response.setStatus("fail");
			return response;
		}else{
			//CustomerLoginService loginService = new CustomerLoginService(request);
			//response = loginService.login();
			response.setStatus("success");
		}
		return response;
	}

}

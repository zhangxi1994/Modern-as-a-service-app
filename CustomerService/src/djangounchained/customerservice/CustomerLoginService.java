package djangounchained.customerservice;

import com.amazonaws.services.dynamodbv2.document.Item;

import djangounchained.util.*;

public class CustomerLoginService {
	private RequestClass request = null;
	private DynamoDbHandler dynamo = null;

	public CustomerLoginService(RequestClass request) {
		this.request = request;
		this.dynamo = new DynamoDbHandler();
	}

	public ResponseClass login() {
		ResponseClass response = new ResponseClass();
		Item customer = dynamo.readCustomer(request.getCustomer().getId());
		if (customer == null || !customer.get("password").equals(request.getCustomer().getPassword())
				|| customer.get("verified").equals("false")) {
			response.setStatus("fail");
			response.setJwt("");
		} else {
			response.setStatus("success");
			response.setJwt(JwtHandler.getInstance().encode(request.getCustomer().getId()));
		}
		return response;
	}
}

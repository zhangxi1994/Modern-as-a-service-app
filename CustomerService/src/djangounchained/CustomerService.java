package djangounchained;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import djangounchained.customerservice.CustomerAccountService;
import djangounchained.customerservice.CustomerInfoService;
import djangounchained.customerservice.CustomerLoginService;
import djangounchained.customerservice.CustomerSignupService;
import djangounchained.util.Customer;
import djangounchained.util.DynamoDbHandler;
import djangounchained.util.JwtHandler;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

import java.util.*;

public class CustomerService implements RequestHandler<RequestClass, ResponseClass> {
	private RequestClass request = null;


	@Override
	public ResponseClass handleRequest(RequestClass input, Context context) {
		this.request = input;
		return handleRequestSelf();
	}

	public ResponseClass handleRequestSelf() {
		ResponseClass response = null;
		String type = request.getType();
		if (type.equals("LogIn")) {
			CustomerLoginService loginService = new CustomerLoginService(request);
			response = loginService.login();
			//Map<String, String> map = new HashMap<>();
			//map.put("Access-Control-Allow-Origin","*");
			//response.setHeaders(map);
		} else if (type.equals("SignUp")) {
			CustomerSignupService signUpService = new CustomerSignupService(request);
			response = signUpService.signup();
			response.setCustomer(request.getCustomer());
			response.setType(request.getType());
		} else if (type.equals("CustomerInfo")) {
			CustomerInfoService infoService = new CustomerInfoService(request.getJwt());
			response = infoService.getCustomerInfo();
		} else if (type.equals("CustomerAccount")) {
			CustomerAccountService accountService = new CustomerAccountService(request.getJwt());
			response = accountService.getAccountInfo();
		}

		return response;
	}

}

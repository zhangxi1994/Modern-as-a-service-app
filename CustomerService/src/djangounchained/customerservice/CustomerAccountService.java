package djangounchained.customerservice;

import java.util.ArrayList;
import java.util.List;

import djangounchained.util.DynamoDbHandler;
import djangounchained.util.JwtHandler;
import djangounchained.util.Order;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

public class CustomerAccountService {
	private DynamoDbHandler dynamo = null;
	private String customerID = null;
	
	public CustomerAccountService(String JWT){
		this.dynamo = new DynamoDbHandler();
		this.customerID = JwtHandler.getInstance().decode(JWT);
	}
	
	public ResponseClass getAccountInfo(){
		List<Order> orders = new ArrayList<>();
		orders =  dynamo.readOrders(customerID);
		ResponseClass response = new ResponseClass();
		response.setOrders(orders);
		if(orders.isEmpty()) response.setStatus("fail");
		else response.setStatus("success");
		return response;
	}
	
}

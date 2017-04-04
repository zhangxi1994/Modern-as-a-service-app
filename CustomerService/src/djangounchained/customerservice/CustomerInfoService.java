package djangounchained.customerservice;

import com.amazonaws.services.dynamodbv2.document.Item;

import djangounchained.util.Customer;
import djangounchained.util.DynamoDbHandler;
import djangounchained.util.JwtHandler;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

public class CustomerInfoService {
	private DynamoDbHandler dynamo = null;
	//private RequestClass request = null;
	private String customerID = null;
	
	public CustomerInfoService(String JWT){
		this.dynamo = new DynamoDbHandler();
		this.customerID = JwtHandler.getInstance().decode(JWT);
	}
	
	public ResponseClass getCustomerInfo(){
		Item outcome = dynamo.readCustomer(customerID);
		if(outcome == null) return null;
		
		ResponseClass response = new ResponseClass();
		response.setStatus("success");
		
		Customer customer = new Customer();
		customer.setDate_of_birth(outcome.getString("date_of_birth"));
		customer.setFirst_name(outcome.getString("first_name"));
		customer.setId(outcome.getString("id"));
		customer.setLast_name(outcome.getString("last_name"));
		customer.setBalance(outcome.getString("balance"));
		
		response.setCustomer(customer);
		return response;
	}

}

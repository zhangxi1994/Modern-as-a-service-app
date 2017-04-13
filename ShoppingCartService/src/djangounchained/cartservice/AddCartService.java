package djangounchained.cartservice;

import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import djangounchained.util.*;

public class AddCartService {
	private String customerID = null;
	private DynamoDbHandler dynamo = null;
	private String itemID = null;

	public AddCartService(String JWT, String item_ID) {
		this.customerID = JwtHandler.getInstance().decode(JWT);
		this.dynamo = new DynamoDbHandler();
		this.itemID = item_ID;
	}

	public ResponseClass addCart() {
		ResponseClass response = new ResponseClass();
		
		String cartID = dynamo.scanCartExist(customerID, itemID);
		if(cartID.length() != 0){
			dynamo.updateCart(cartID);
			response.setStatus("success");
			return response;
		}
		
		PutItemOutcome outcome = dynamo.addCart(customerID, itemID);
		if (outcome == null) {
			response.setStatus("fail");
		} else {
			response.setStatus("success");
		}
		
		return response;
	}
}

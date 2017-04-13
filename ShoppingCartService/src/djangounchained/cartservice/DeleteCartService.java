package djangounchained.cartservice;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

import djangounchained.util.DynamoDbHandler;
import djangounchained.util.JwtHandler;
import djangounchained.util.ResponseClass;

public class DeleteCartService {
	private String customerID = null;
	private DynamoDbHandler dynamo = null;
	private String itemID = null;

	public DeleteCartService(String JWT, String item_ID) {
		this.customerID = JwtHandler.getInstance().decode(JWT);
		this.dynamo = new DynamoDbHandler();
		this.itemID = item_ID;
	}

	public ResponseClass deleteCart() {
		ResponseClass response = new ResponseClass();
		
		DeleteItemOutcome outcome = dynamo.deleteCart(customerID, itemID);
		if (outcome == null) {
			response.setStatus("fail");
		} else {
			response.setStatus("success");
		}
		return response;
	}
}

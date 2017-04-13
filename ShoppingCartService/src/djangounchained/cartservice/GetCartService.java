package djangounchained.cartservice;

import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

import djangounchained.util.*;

public class GetCartService {
	private String customerID = null;
	private DynamoDbHandler dynamo = null;

	public GetCartService(String JWT) {
		this.customerID = JwtHandler.getInstance().decode(JWT);
		this.dynamo = new DynamoDbHandler();
	}

	public ResponseClass getCart() {
		ResponseClass response = new ResponseClass();
		List<CartItem> cart = dynamo.readCart(customerID);
		if (cart.isEmpty()) {
			response.setStatus("fail");
		} else {
			response.setStatus("success");
			response.setItems(cart);
		}
		return response;
	}
}

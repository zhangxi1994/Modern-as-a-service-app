package djangounchained.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;

public class DynamoDbHandler {
	// This client will default to US West (Oregon)
	private AmazonDynamoDBClient client = null;
	private static DynamoDB dynamoDB = null;

	// Modify the client so that it accesses a different region.
	// client.withRegion(Regions.US_EAST_1);
	public DynamoDbHandler() {
		client = new AmazonDynamoDBClient(new EnvironmentVariableCredentialsProvider());
		client.withRegion(Regions.US_WEST_2);
		dynamoDB = new DynamoDB(client);
	}

	public Item read(String id, String tableName) {
		Table table = dynamoDB.getTable(tableName);
		Item outcome = null;
		GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", id);
		try {
			outcome = table.getItem(spec);
		} catch (Exception e) {

		}
		return outcome;
	}

	public Item readCustomer(String id) {
		return this.read(id, "Customer");
	}

	public PutItemOutcome createCustomer(RequestClass request) {
		// if already exist
		if (read(request.getCustomer().getId(), "Customer") != null)
			return null;

		Table table = dynamoDB.getTable("Customer");
		PutItemOutcome outcome = null;

		Item item = new Item().withPrimaryKey("id", request.getCustomer().getId())
				.withString("first_name", request.getCustomer().getFirst_name())
				.withString("last_name", request.getCustomer().getLast_name())
				.withString("password", request.getCustomer().getPassword())
				.withString("date_of_birth", request.getCustomer().getDate_of_birth()).withString("balance", "0")
				.withString("verified", "false").withString("hash_val", "null");
		outcome = table.putItem(item);

		return outcome;
	}

	public List<Order> readOrders(String userId) {
		List<Order> orders = new ArrayList<>();

		Table table = dynamoDB.getTable("Order");
		
		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":user_id", userId);

		ItemCollection<ScanOutcome> items = table.scan("user_id = :user_id", // FilterExpression
				"id, address, item_id, item_name, price, date_time, user_id", // ProjectionExpression
				null, // ExpressionAttributeNames - not used in this example
				expressionAttributeValues);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			Order order = new Order();
			order.setAddress(item.getString("address"));
			order.setId(item.getString("id"));
			order.setItem_id(item.getString("item_id"));
			order.setItem_name(item.getString("item_name"));
			//order.setPayment_method(item.getString("payment_method"));
			order.setPrice(item.getString("price"));
			order.setTime(item.getString("date_time"));
			orders.add(order);
		}
		return orders;
	}
}

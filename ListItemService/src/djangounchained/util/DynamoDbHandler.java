package djangounchained.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
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

	public String scanCartExist(String userId, String itemID) {

		Table table = dynamoDB.getTable("Cart");

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":user_id", userId);

		ItemCollection<ScanOutcome> items = table.scan("user_id = :user_id", // FilterExpression
				"id, item_id, user_id, quantity", // ProjectionExpression
				null, // ExpressionAttributeNames - not used in this example
				expressionAttributeValues);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			if (item.getString("item_id").equals(itemID))
				return item.getString("id");
		}

		return "";
	}

	public PutItemOutcome addCart(String user_id, String item_id) {

		Table table = dynamoDB.getTable("Cart");
		PutItemOutcome outcome = null;

		Item item = new Item().withPrimaryKey("id", GenerateUUID.generate()).withString("user_id", user_id)
				.withString("item_id", item_id)
				.withInt("quantity", 1);
		outcome = table.putItem(item);

		return outcome;
	}

	public UpdateItemOutcome updateCart(String id) {
		Table table = dynamoDB.getTable("Cart");

		Map<String, String> expressionAttributeNames = new HashMap<String, String>();
		expressionAttributeNames.put("#Q", "quantity");

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":val1", 1); // number

		UpdateItemOutcome outcome = table.updateItem("id", // key attribute
				id, "set #Q = #Q + :val1", // UpdateExpression
				expressionAttributeNames, expressionAttributeValues);
		return outcome;
	}
	public DeleteItemOutcome deleteCart(String user_id, String item_id){
		Table table = dynamoDB.getTable("Cart");
		
		String id = this.scanCartExist(user_id, item_id);
		DeleteItemOutcome outcome = table.deleteItem("id",id);
		return outcome;
	}
	
	public List<MyItem> readAllItem() {
		List<MyItem> allItems = new ArrayList<>();

		Table table = dynamoDB.getTable("Item");

		Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
		expressionAttributeValues.put(":val1", 0);

		ItemCollection<ScanOutcome> items = table.scan("in_stock > :val1", // FilterExpression
				"id, description, in_stock, item_name, price", // ProjectionExpression
				null, // ExpressionAttributeNames - not used in this example
				expressionAttributeValues);

		Iterator<Item> iterator = items.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			MyItem tmp = new MyItem();
			tmp.setId(item.getString("id"));
			tmp.setItem_name(item.getString("item_name"));
			tmp.setPrice(item.getString("price"));
			tmp.setDescription(item.getString("description"));
			tmp.setIn_stock(item.getInt("in_stock"));
			allItems.add(tmp);
		}
		return allItems;
	}
}

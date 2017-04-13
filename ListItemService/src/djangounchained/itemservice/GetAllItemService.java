package djangounchained.itemservice;

import java.util.List;
import djangounchained.util.*;

public class GetAllItemService {
	private DynamoDbHandler dynamo = null;

	public GetAllItemService() {
		this.dynamo = new DynamoDbHandler();
	}

	public ResponseClass getAllItem() {
		ResponseClass response = new ResponseClass();
		List<MyItem> allItems = dynamo.readAllItem();
		if (allItems.isEmpty()) {
			response.setStatus("fail");
		} else {
			response.setStatus("success");
			response.setItems(allItems);
		}
		return response;
	}
}

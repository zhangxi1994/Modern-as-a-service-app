package djangounchained;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import djangounchained.itemservice.GetAllItemService;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

public class ListItemService implements RequestHandler<RequestClass, ResponseClass> {
	private RequestClass request = null;

	@Override
	public ResponseClass handleRequest(RequestClass input, Context context) {
		this.request = input;
		return handleRequestSelf();
	}

	public ResponseClass handleRequestSelf() {
		ResponseClass response = null;
		String type = request.getType();
		if(type.equals("ListAllItems")){
			GetAllItemService service = new GetAllItemService();
			response = service.getAllItem();
		}
		return response;
	}
}

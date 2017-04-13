package djangounchained;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import djangounchained.cartservice.AddCartService;
import djangounchained.cartservice.DeleteCartService;
import djangounchained.cartservice.GetCartService;
import djangounchained.util.RequestClass;
import djangounchained.util.ResponseClass;

public class ShoppingCartService implements RequestHandler<RequestClass, ResponseClass> {
	private RequestClass request = null;

	@Override
	public ResponseClass handleRequest(RequestClass input, Context context) {
		this.request = input;
		return handleRequestSelf();
	}

	public ResponseClass handleRequestSelf() {
		ResponseClass response = null;
		String type = request.getType();
		if (type.equals("GetCart")) {
			GetCartService service = new GetCartService(request.getJwt());
			response = service.getCart();
		}else if(type.equals("AddCart")){
			AddCartService service = new AddCartService(request.getJwt(), request.getItem_id());
			response = service.addCart();
		}else if(type.equals("DeleteCart")){
			DeleteCartService service = new DeleteCartService(request.getJwt(), request.getItem_id());
			response = service.deleteCart();
		}

		return response;
	}
}

package djangounchained.util;

import java.util.List;
import java.util.Map;

public class ResponseClass {
	private String status = null; // pending success fail
	private String jwt = null;
	private Customer customer = null;
	private List<CartItem> items = null;
	private String type = null;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<CartItem> getItems() {
		return items;
	}
	public void setItems(List<CartItem> orders) {
		this.items = orders;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	
	
}

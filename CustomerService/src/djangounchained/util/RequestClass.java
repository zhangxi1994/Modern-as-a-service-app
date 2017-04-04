package djangounchained.util;

import java.util.List;

public class RequestClass {
	private String operation = null;
	private String resource = null;
	private String type = null; // LogIn/SignUp CustomerInfo CustomerAccount
//	private String id = null;
//	private String first_name = null;
//	private String last_name = null;
//	private String password = null;
//	private String date_of_birth = null;
	private Customer customer = null;
	private String jwt = null;
	
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public String getJwt() {
		return jwt;
	}
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}

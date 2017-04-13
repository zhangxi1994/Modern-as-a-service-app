package djangounchained.util;

public class CartItem {
	private String id = null;
	private String item_name = null;
	//private String price = null;
	private String description = null;
	private int quantity = 0;
	private int price = 0;
	
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return item_name;
	}
	public void setName(String name) {
		this.item_name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
